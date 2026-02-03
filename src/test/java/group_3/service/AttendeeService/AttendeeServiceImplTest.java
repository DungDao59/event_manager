package group_3.service.AttendeeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.PersonDAO;
import group_3.model.Attendee;

class AttendeeServiceImplTest {

    private AttendeeServiceImpl service;
    private PersonDAO personDAOMock;

    @BeforeEach
    void setUp() {
        personDAOMock = mock(PersonDAO.class);
        service = new AttendeeServiceImpl(personDAOMock);
    }

    @Test
    void createAttendee_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createAttendee(null));
    }

    @Test
    void createAttendee_success_callsDao() {
        Attendee a = new Attendee(0, "u1", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        Attendee res = service.createAttendee(a);
        assertEquals(a, res);
        verify(personDAOMock).create(a);
    }

    @Test
    void getAttendeeById_notFound_returnsEmpty() {
        when(personDAOMock.findById(5)).thenReturn(Optional.empty());
        assertTrue(service.getAttendeeById(5).isEmpty());
    }

    @Test
    void getAttendeeByUsername_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getAttendeeByUsername(""));
        assertThrows(IllegalArgumentException.class, () -> service.getAttendeeByUsername(null));
    }

    @Test
    void getAllAttendees_filtersToAttendees() {
        Attendee a1 = new Attendee(1, "a1", "p", "A1", java.time.LocalDate.now(), "{}", "{}");
        group_3.model.Presenter p = new group_3.model.Presenter(2, "pr", "p", "P", java.time.LocalDate.now(), "", "", "");
        when(personDAOMock.findAll()).thenReturn(List.of(a1, p));

        List<Attendee> res = service.getAllAttendees();
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
    }

    @Test
    void updateHistory_attendeeNotFound_throws() {
        when(personDAOMock.findById(10)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.updateHistory(10, "h"));
    }

    @Test
    void updateHistory_success_updatesAndCallsDao() {
        Attendee a = new Attendee(3, "x", "p", "X", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(3)).thenReturn(Optional.of(a));
        service.updateHistory(3, "newhistory");
        assertEquals("newhistory", a.getHistory());
        verify(personDAOMock).update(a);
    }

    @Test
    void updateAttendee_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateAttendee(null));
    }

    @Test
    void deleteAttendee_callsDao() {
        service.deleteAttendee(7);
        verify(personDAOMock).delete(7);
    }

    @Test
    void attendeeExists_behaviour() {
        Attendee a = new Attendee(4, "u4", "p", "N", java.time.LocalDate.now(), "{}", "{}");
        when(personDAOMock.findById(4)).thenReturn(Optional.of(a));
        assertTrue(service.attendeeExists(4));
        assertFalse(service.attendeeExists(5));
    }

    @Test
    void getTotalAttendeeCount_countsOnlyAttendees() {
        Attendee a = new Attendee(1, "a1", "p", "A1", java.time.LocalDate.now(), "{}", "{}");
        group_3.model.Presenter p = new group_3.model.Presenter(2, "pr", "p", "P", java.time.LocalDate.now(), "", "", "");
        when(personDAOMock.findAll()).thenReturn(List.of(a, p));
        assertEquals(1, service.getTotalAttendeeCount());
    }
}
