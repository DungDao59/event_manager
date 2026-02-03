package group_3.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.PersonDAO;
import group_3.model.Person;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.security.AuthContext;
import group_3.util.PasswordUtil;

class AuthServiceImplTest {

    private AuthServiceImpl service;
    private PersonDAO personDAOMock;
    private SystemHistoryService historyMock;

    @BeforeEach
    void setUp() throws Exception {
        service = new AuthServiceImpl();
        personDAOMock = mock(PersonDAO.class);
        historyMock = mock(SystemHistoryService.class);
        setField(service, "personDAO", personDAOMock);
        setField(service, "historyService", historyMock);
        AuthContext.clear();
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void login_success_and_authState() throws Exception {
        // Use a real Attendee instance instead of mocking Person to avoid inline-mock issues on modern JDKs
        group_3.model.Attendee p = new group_3.model.Attendee(7, "u", PasswordUtil.hash("secret"), "User", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("u")).thenReturn(Optional.of(p));

        var res = service.login("u", "secret");
        assertTrue(res.isPresent());
        assertTrue(service.isAuthenticated());
        assertEquals(p, service.getCurrentUser());

        // logout
        service.logout();
        // allow async logger thread to run (if any)
        Thread.sleep(50);
        assertFalse(service.isAuthenticated());
    }

    @Test
    void login_failure_wrongPassword() {
        // Use concrete Attendee to avoid mocking Person class
        group_3.model.Attendee p = new group_3.model.Attendee(8, "u2", PasswordUtil.hash("secret"), "User2", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findByUsername("u2")).thenReturn(Optional.of(p));

        var res = service.login("u2", "bad");
        assertTrue(res.isEmpty());
    }
}
