package group_3.service.EventAdminService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.EventDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.model.Event;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.security.AuthContext;

class EventAdminServiceImplTest {

    private EventAdminServiceImpl service;
    private EventDAO eventDAOMock;
    private SessionDAO sessionDAOMock;
    private TicketDAO ticketDAOMock;
    private SystemHistoryService historyMock;

    @BeforeEach
    void setUp() throws Exception {
        eventDAOMock = mock(EventDAO.class);
        sessionDAOMock = mock(SessionDAO.class);
        ticketDAOMock = mock(TicketDAO.class);
        historyMock = mock(SystemHistoryService.class);

        service = new EventAdminServiceImpl(eventDAOMock, sessionDAOMock, ticketDAOMock);
        // inject history mock
        var f = service.getClass().getDeclaredField("historyService");
        f.setAccessible(true);
        f.set(service, historyMock);

        AuthContext.clear();
    }

    @Test
    void createEvent_valid_and_invalid() {
        Event e = new Event(0, "Ev", EventType.CONFERENCE,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "L", 1, EventStatus.SCHEDULED, null);

        when(eventDAOMock.count()).thenReturn(0);
        service.createEvent(e);
        verify(eventDAOMock).create(e);

        assertThrows(IllegalArgumentException.class, () -> service.createEvent(new Event()));
    }

    @Test
    void createSession_validates_times_and_eventBounds() {
        Event ev = new Event(1, "E", EventType.CONFERENCE, LocalDateTime.of(2026,1,1,0,0), LocalDateTime.of(2026,1,3,0,0), "L", 1, EventStatus.SCHEDULED, null);
        when(eventDAOMock.findById(1)).thenReturn(Optional.of(ev));

        Session good = new Session(0, 1, "T", "D", LocalDateTime.of(2026,1,1,10,0), LocalDateTime.of(2026,1,1,11,0), "V", 10);
        service.createSession(good);
        verify(sessionDAOMock).create(good);

        Session badStart = new Session(0, 1, "T", "D", LocalDateTime.of(2025,12,31,10,0), LocalDateTime.of(2026,1,1,11,0), "V", 10);
        assertThrows(IllegalArgumentException.class, () -> service.createSession(badStart));

        Session badEnd = new Session(0, 1, "T", "D", LocalDateTime.of(2026,1,3,1,0), LocalDateTime.of(2026,1,3,2,0), "V", 10);
        assertThrows(IllegalArgumentException.class, () -> service.createSession(badEnd));
    }

    @Test
    void assign_and_unassign_presenters_updates_session() {
        Session s = new Session(10, 1, "T", "D", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "V", 10);
        when(sessionDAOMock.findById(10)).thenReturn(Optional.of(s));
        when(sessionDAOMock.exists(10)).thenReturn(true);

        boolean assigned = service.assignPresenterToSession(10, 5);
        assertTrue(assigned);
        verify(sessionDAOMock).update(any());

        boolean assignedAgain = service.assignPresenterToSession(10, 5);
        assertFalse(assignedAgain);

        boolean unassigned = service.unassignPresenterFromSession(10, 5);
        assertTrue(unassigned);
    }

    @Test
    void generate_and_update_ticket_behaviour() {
        when(eventDAOMock.exists(1)).thenReturn(true);
        when(sessionDAOMock.exists(0)).thenReturn(true);

        when(ticketDAOMock.create(any())).thenAnswer(inv -> {
            Ticket t = (Ticket) inv.getArgument(0);
            t.setTicketID(42);
            return 42;
        });

        Ticket t = service.generateTicket(1, 1, 0, TicketType.GENERAL, 10.0);
        assertEquals(42, t.getTicketID());

        when(ticketDAOMock.findById(42)).thenReturn(t);
        boolean updated = service.updateTicketStatus(42, TicketStatus.CANCELLED);
        assertTrue(updated);
    }
}
