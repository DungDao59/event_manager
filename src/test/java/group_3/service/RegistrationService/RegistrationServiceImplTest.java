package group_3.service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.model.ScheduleEntry;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.security.AuthContext;
import group_3.model.Attendee;

class RegistrationServiceImplTest {

    private RegistrationServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new RegistrationServiceImpl();

        // Create mocks and inject into the private final fields
        TicketDAO mockTicketDAO = mock(TicketDAO.class);
        ScheduleDAO mockScheduleDAO = mock(ScheduleDAO.class);
        SessionDAO mockSessionDAO = mock(SessionDAO.class);
        SystemHistoryService mockHistory = mock(SystemHistoryService.class);

        setField(service, "ticketDAO", mockTicketDAO);
        setField(service, "scheduleDAO", mockScheduleDAO);
        setField(service, "sessionDAO", mockSessionDAO);
        setField(service, "historyService", mockHistory);

        // ensure no current user unless tests set one explicitly
        AuthContext.clear();
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void checkPersonConflict_detectsOverlap() {
        ScheduleEntry exists = new ScheduleEntry();
        exists.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        exists.setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0));

        ScheduleDAO scheduleDAO = (ScheduleDAO) getField(service, "scheduleDAO");
        when(scheduleDAO.findAllScheduleByUserId(1)).thenReturn(new ArrayList<ScheduleEntry>() {{ add(exists); }});

        Session session = new Session(5, 1, "S1", "desc",
                LocalDateTime.of(2025, 1, 1, 10, 30), LocalDateTime.of(2025, 1, 1, 12, 0), "v", 100);

        SessionDAO sessionDAO = (SessionDAO) getField(service, "sessionDAO");
        when(sessionDAO.findById(5)).thenReturn(Optional.of(session));

        assertTrue(service.checkPersonConflict(1, 5));
    }

    @Test
    void registerAttendee_successfulFlow() {
        // Arrange: no conflict
        ScheduleDAO scheduleDAO = (ScheduleDAO) getField(service, "scheduleDAO");
        when(scheduleDAO.findAllScheduleByUserId(10)).thenReturn(new ArrayList<>());

        SessionDAO sessionDAO = (SessionDAO) getField(service, "sessionDAO");
        Session s = new Session(7, 99, "Title", "Desc",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "V", 100);
        when(sessionDAO.findById(7)).thenReturn(Optional.of(s));

        TicketDAO ticketDAO = (TicketDAO) getField(service, "ticketDAO");
        when(ticketDAO.create(any())).thenReturn(123);

        // make current user present for history & notification
        Attendee user = new Attendee(42, "u42", "p", "User 42", java.time.LocalDate.now(), "{}", "{}");
        AuthContext.setCurrentUser(user);

        // Act
        boolean res = service.registerAttendee(10, 7, TicketType.GENERAL, 15.00);

        // Assert
        assertTrue(res);
        verify(ticketDAO).create(any());
        verify(ticketDAO).update(any());
        verify(scheduleDAO).create(any());

        SystemHistoryService history = (SystemHistoryService) getField(service, "historyService");
        verify(history).logAction(any(), eq("REGISTER ATTENDEE"), anyString());
    }

    @Test
    void registerAttendee_sessionMissing_returnsFalse() {
        SessionDAO sessionDAO = (SessionDAO) getField(service, "sessionDAO");
        when(sessionDAO.findById(99)).thenReturn(Optional.empty());

        boolean res = service.registerAttendee(1, 99, TicketType.GENERAL, 0.0);
        assertFalse(res);
    }

    @Test
    void registerAttendee_ticketCreationFails_returnsFalse() {
        ScheduleDAO scheduleDAO = (ScheduleDAO) getField(service, "scheduleDAO");
        when(scheduleDAO.findAllScheduleByUserId(1)).thenReturn(new ArrayList<>());

        SessionDAO sessionDAO = (SessionDAO) getField(service, "sessionDAO");
        Session s = new Session(2, 1, "T", "D", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "V", 20);
        when(sessionDAO.findById(2)).thenReturn(Optional.of(s));

        TicketDAO ticketDAO = (TicketDAO) getField(service, "ticketDAO");
        when(ticketDAO.create(any())).thenReturn(-1);

        boolean res = service.registerAttendee(1, 2, TicketType.GENERAL, 10.0);
        assertFalse(res);
    }

    @Test
    void cancelRegistration_handlesNotFound_andSuccess() {
        TicketDAO ticketDAO = (TicketDAO) getField(service, "ticketDAO");
        when(ticketDAO.findById(999)).thenReturn(null);
        assertFalse(service.cancelRegistration(999));

        // success path
        Ticket ticket = new Ticket();
        ticket.setTicketID(5);
        ticket.setAttendeeID(10);
        ticket.setSessionID(11);

        when(ticketDAO.findById(5)).thenReturn(ticket);

        ScheduleDAO scheduleDAO = (ScheduleDAO) getField(service, "scheduleDAO");

        boolean res = service.cancelRegistration(5);
        assertTrue(res);
        verify(ticketDAO).update(any());
        verify(scheduleDAO).deleteByUserAndSession(10, 11);

        SystemHistoryService history = (SystemHistoryService) getField(service, "historyService");
        verify(history).logAction(any(), eq("CANCEL REGISTRATION"), contains("Cancel ticket ID"));
    }

    private Object getField(Object target, String name) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
