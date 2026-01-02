package group_3.service.EventStatisticsService;

import group_3.dao.EventDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.model.*;
import group_3.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventStatisticsServiceImpl.
 * Uses Mockito to mock DAO dependencies.
 * 
 * @author Group 3
 */
class EventStatisticsServiceTest {
    
    @Mock
    private EventDAO eventDAO;
    
    @Mock
    private SessionDAO sessionDAO;
    
    @Mock
    private TicketDAO ticketDAO;
    
    private EventStatisticsService statsService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statsService = new EventStatisticsServiceImpl(eventDAO, sessionDAO, ticketDAO);
    }
    
    @Test
    void testCalculateEventRevenue_WithValidTickets() {
        // Arrange
        int eventId = 1;
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.ACTIVE));
        tickets.add(createTicket(2, eventId, 1, 2, 150.0, TicketStatus.USED));
        tickets.add(createTicket(3, eventId, 2, 3, 200.0, TicketStatus.CANCELLED)); // Should be excluded
        tickets.add(createTicket(4, 2, 1, 4, 75.0, TicketStatus.ACTIVE)); // Different event, excluded
        
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        double revenue = statsService.calculateEventRevenue(eventId);
        
        // Assert
        assertEquals(250.0, revenue, 0.01); // 100 + 150
    }
    
    @Test
    void testCalculateEventRevenue_NoTickets() {
        // Arrange
        when(ticketDAO.findAll()).thenReturn(new ArrayList<>());
        
        // Act
        double revenue = statsService.calculateEventRevenue(1);
        
        // Assert
        assertEquals(0.0, revenue, 0.01);
    }
    
    @Test
    void testCalculateEventAttendanceRate_FullAttendance() {
        // Arrange
        int eventId = 1;
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.USED));
        tickets.add(createTicket(2, eventId, 1, 2, 150.0, TicketStatus.USED));
        
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        double rate = statsService.calculateEventAttendanceRate(eventId);
        
        // Assert
        assertEquals(100.0, rate, 0.01);
    }
    
    @Test
    void testCalculateEventAttendanceRate_PartialAttendance() {
        // Arrange
        int eventId = 1;
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.USED));
        tickets.add(createTicket(2, eventId, 1, 2, 150.0, TicketStatus.ACTIVE));
        tickets.add(createTicket(3, eventId, 2, 3, 200.0, TicketStatus.ACTIVE));
        tickets.add(createTicket(4, eventId, 2, 4, 75.0, TicketStatus.USED));
        
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        double rate = statsService.calculateEventAttendanceRate(eventId);
        
        // Assert
        assertEquals(50.0, rate, 0.01); // 2 checked in out of 4 sold
    }
    
    @Test
    void testCalculateEventAttendanceRate_NoTickets() {
        // Arrange
        when(ticketDAO.findAll()).thenReturn(new ArrayList<>());
        
        // Act
        double rate = statsService.calculateEventAttendanceRate(1);
        
        // Assert
        assertEquals(0.0, rate, 0.01);
    }
    
    @Test
    void testGetEventStatistics_ValidEvent() {
        // Arrange
        int eventId = 1;
        Event event = createEvent(1, "Tech Conference 2025");
        
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.USED));
        tickets.add(createTicket(2, eventId, 1, 2, 150.0, TicketStatus.ACTIVE));
        
        when(eventDAO.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        Optional<EventStatistics> statsOpt = statsService.getEventStatistics(eventId);
        
        // Assert
        assertTrue(statsOpt.isPresent());
        EventStatistics stats = statsOpt.get();
        assertEquals(eventId, stats.getEventId());
        assertEquals("Tech Conference 2025", stats.getEventName());
        assertEquals(250.0, stats.getTotalRevenue(), 0.01);
        assertEquals(2, stats.getTotalTicketsSold());
        assertEquals(1, stats.getTotalCheckedIn());
        assertEquals(50.0, stats.getAttendanceRate(), 0.01);
    }
    
    @Test
    void testGetEventStatistics_EventNotFound() {
        // Arrange
        when(eventDAO.findById(999)).thenReturn(Optional.empty());
        
        // Act
        Optional<EventStatistics> statsOpt = statsService.getEventStatistics(999);
        
        // Assert
        assertFalse(statsOpt.isPresent());
    }
    
    @Test
    void testGetSessionStatistics_ValidSession() {
        // Arrange
        int sessionId = 1;
        int eventId = 1;
        Session session = createSession(1, 1, "Opening Keynote", 200);
        
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, sessionId, 1, 100.0, TicketStatus.USED));
        tickets.add(createTicket(2, eventId, sessionId, 2, 100.0, TicketStatus.USED));
        tickets.add(createTicket(3, eventId, sessionId, 3, 100.0, TicketStatus.ACTIVE));
        
        when(sessionDAO.findById(sessionId)).thenReturn(Optional.of(session));
        when(ticketDAO.findTicketBySessionId(sessionId)).thenReturn(tickets);
        
        // Act
        Optional<SessionStatistics> statsOpt = statsService.getSessionStatistics(sessionId);
        
        // Assert
        assertTrue(statsOpt.isPresent());
        SessionStatistics stats = statsOpt.get();
        assertEquals(sessionId, stats.getSessionId());
        assertEquals("Opening Keynote", stats.getSessionTitle());
        assertEquals(3, stats.getTotalTicketsSold());
        assertEquals(2, stats.getTotalCheckedIn());
        assertEquals(66.67, stats.getAttendanceRate(), 0.1);
        assertEquals(1.5, stats.getCapacityUtilization(), 0.1); // 3/200 * 100
    }
    
    @Test
    void testGetMostPopularSessions_ReturnsSortedByTickets() {
        // Arrange
        int eventId = 1;
        
        Session session1 = createSession(1, 1, "Session A", 100);
        Session session2 = createSession(2, 1, "Session B", 100);
        Session session3 = createSession(3, 1, "Session C", 100);
        
        when(sessionDAO.findByEventId(eventId)).thenReturn(Arrays.asList(session1, session2, session3));
        
        // Session 1 has 5 tickets
        ArrayList<Ticket> tickets1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tickets1.add(createTicket(i, eventId, 1, i, 100.0, TicketStatus.ACTIVE));
        }
        
        // Session 2 has 10 tickets
        ArrayList<Ticket> tickets2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tickets2.add(createTicket(i + 5, eventId, 2, i, 100.0, TicketStatus.ACTIVE));
        }
        
        // Session 3 has 3 tickets
        ArrayList<Ticket> tickets3 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tickets3.add(createTicket(i + 15, eventId, 3, i, 100.0, TicketStatus.ACTIVE));
        }
        
        when(ticketDAO.findTicketBySessionId(1)).thenReturn(tickets1);
        when(ticketDAO.findTicketBySessionId(2)).thenReturn(tickets2);
        when(ticketDAO.findTicketBySessionId(3)).thenReturn(tickets3);
        
        // Act
        List<SessionStatistics> popular = statsService.getMostPopularSessions(eventId, 2);
        
        // Assert
        assertEquals(2, popular.size());
        assertEquals("Session B", popular.get(0).getSessionTitle()); // 10 tickets
        assertEquals("Session A", popular.get(1).getSessionTitle()); // 5 tickets
    }
    
    @Test
    void testGetTotalTicketsSold_ExcludesCancelledTickets() {
        // Arrange
        int eventId = 1;
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.ACTIVE));
        tickets.add(createTicket(2, eventId, 1, 2, 100.0, TicketStatus.USED));
        tickets.add(createTicket(3, eventId, 1, 3, 100.0, TicketStatus.CANCELLED));
        
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        int total = statsService.getTotalTicketsSold(eventId);
        
        // Assert
        assertEquals(2, total); // Only ACTIVE and USED
    }
    
    @Test
    void testGetTotalCheckedIn_OnlyCountsUsedTickets() {
        // Arrange
        int eventId = 1;
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(createTicket(1, eventId, 1, 1, 100.0, TicketStatus.ACTIVE));
        tickets.add(createTicket(2, eventId, 1, 2, 100.0, TicketStatus.USED));
        tickets.add(createTicket(3, eventId, 1, 3, 100.0, TicketStatus.USED));
        
        when(ticketDAO.findAll()).thenReturn(tickets);
        
        // Act
        int checkedIn = statsService.getTotalCheckedIn(eventId);
        
        // Assert
        assertEquals(2, checkedIn);
    }
    
    // Helper methods
    
    private Ticket createTicket(int ticketId, int eventId, int sessionId, int attendeeId, 
                               double price, TicketStatus status) {
        return new Ticket(ticketId, eventId, sessionId, attendeeId, 
                         TicketType.GENERAL, price, status, "qr_path");
    }
    
    private Event createEvent(int eventId, String name) {
        return new Event(eventId, name, EventType.CONFERENCE, 
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                        "Convention Center", 1, EventStatus.SCHEDULED);
    }
    
    private Session createSession(int sessionId, int eventId, String title, int capacity) {
        return new Session(sessionId, eventId, title, "Description", 
                          LocalDateTime.now(), LocalDateTime.now().plusHours(2), 
                          "Main Hall", capacity);
    }
}
