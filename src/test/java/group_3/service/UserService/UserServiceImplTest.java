package group_3.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.PersonDAO;
import group_3.dao.SessionPresenterDAO;
import group_3.model.Attendee;
import group_3.model.Person;
import group_3.model.enums.Role;
import group_3.service.SystemHistoryService.SystemHistoryService;

class UserServiceImplTest {

    private UserServiceImpl service;
    private PersonDAO personDAOMock;
    private SystemHistoryService historyMock;
    private SessionPresenterDAO sessionPresenterMock;

    @BeforeEach
    void setUp() throws Exception {
        personDAOMock = mock(PersonDAO.class);
        historyMock = mock(SystemHistoryService.class);
        // construct service with injectable historyService via constructor
        service = new UserServiceImpl(personDAOMock, historyMock, null);

        // sessionPresenterDAO is created inside the impl; replace it with mock
        sessionPresenterMock = mock(SessionPresenterDAO.class);
        setField(service, "sessionPresenterDAO", sessionPresenterMock);
    }

    @Test
    void createPerson_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(null));
    }

    @Test
    void createPerson_blankUsername_throws() {
        Person p = new Attendee(0, "", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(p));
    }

    @Test
    void createPerson_usernameExists_throws() {
        Person p = new Attendee(0, "u1", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("u1")).thenReturn(Optional.of(p));
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(p));
    }

    @Test
    void createPerson_success_logs() {
        Attendee p = new Attendee(0, "u2", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("u2")).thenReturn(Optional.empty());

        Person res = service.createPerson(p);
        assertEquals(p, res);
        verify(personDAOMock).create(p);
        verify(historyMock).logAction(isNull(), eq("USER_CREATED"), anyString());
    }

    @Test
    void getUserById_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getUserById(0));
    }

    @Test
    void getUserById_found_returns() {
        Attendee p = new Attendee(5, "u5", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(5)).thenReturn(Optional.of(p));
        Optional<Person> opt = service.getUserById(5);
        assertTrue(opt.isPresent());
        assertEquals(5, opt.get().getId());
    }

    @Test
    void getUserByUsername_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getUserByUsername(""));
    }

    @Test
    void getUserByUsername_found_returns() {
        Attendee p = new Attendee(6, "u6", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("u6")).thenReturn(Optional.of(p));
        Optional<Person> opt = service.getUserByUsername("u6");
        assertTrue(opt.isPresent());
        assertEquals("u6", opt.get().getUsername());
    }

    @Test
    void updateUser_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateUser(null));
        Attendee p = new Attendee(0, "u", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        assertThrows(IllegalArgumentException.class, () -> service.updateUser(p));
    }

    @Test
    void updateUser_notFound_throws() {
        Attendee p = new Attendee(8, "u8", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(8)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.updateUser(p));
    }

    @Test
    void updateUser_success_logsAndUpdates() {
        Attendee p = new Attendee(9, "u9", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(9)).thenReturn(Optional.of(p));

        service.updateUser(p);
        verify(personDAOMock).update(p);
        verify(historyMock).logAction(eq(9), eq("USER_UPDATED"), contains("\"userId\": 9"));
    }

    @Test
    void deleteUser_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteUser(0));
    }

    @Test
    void deleteUser_notFound_throws() {
        when(personDAOMock.findById(7)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.deleteUser(7));
    }

    @Test
    void deleteUser_presenterAssigned_throws() {
        Attendee p = new Attendee(11, "u11", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(11)).thenReturn(Optional.of(p));
        when(sessionPresenterMock.existsByPresenterId(11)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.deleteUser(11));
    }

    @Test
    void deleteUser_success_deletesAndLogs() {
        Attendee p = new Attendee(12, "u12", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(12)).thenReturn(Optional.of(p));
        when(sessionPresenterMock.existsByPresenterId(12)).thenReturn(false);

        service.deleteUser(12);
        verify(personDAOMock).delete(12);
        verify(historyMock).logAction(eq(12), eq("USER_DELETED"), contains("\"userId\": 12"));
    }

    @Test
    void userExists_behavior() {
        Attendee a = new Attendee(3, "x", "p", "X", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("x")).thenReturn(Optional.of(a));
        assertTrue(service.userExists("x"));
        assertFalse(service.userExists(null));
        assertFalse(service.userExists(""));
    }

    @Test
    void assignRole_validations_and_change() {
        assertThrows(IllegalArgumentException.class, () -> service.assignRole(0, Role.PRESENTER));
        assertThrows(IllegalArgumentException.class, () -> service.assignRole(1, null));

        Attendee p = new Attendee(20, "u20", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        p.setRole(Role.ATTENDEE);
        when(personDAOMock.findById(20)).thenReturn(Optional.of(p));

        // no-change
        boolean noChange = service.assignRole(20, Role.ATTENDEE);
        assertFalse(noChange);

        // change
        boolean changed = service.assignRole(20, Role.PRESENTER);
        assertTrue(changed);
        verify(personDAOMock, atLeastOnce()).update(p);
        verify(historyMock, atLeastOnce()).logAction(eq(20), eq("ROLE_CHANGED"), anyString());
    }

    @Test
    void getUsersByRole_filters() {
        Attendee a1 = new Attendee(1, "a1", "p", "A1", java.time.LocalDate.now(), "{}", "{}");
        a1.setRole(Role.ATTENDEE);
        Attendee a2 = new Attendee(2, "a2", "p", "A2", java.time.LocalDate.now(), "{}", "{}");
        a2.setRole(Role.PRESENTER);
        when(personDAOMock.findAll()).thenReturn(List.of(a1, a2));

        var res = service.getUsersByRole(Role.PRESENTER);
        assertEquals(1, res.size());
        assertEquals(Role.PRESENTER, res.get(0).getRole());
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}
