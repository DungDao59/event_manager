package group_3.service.EventStatisticsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import group_3.dao.EventDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.model.Event;
import group_3.model.EventStatistics;
import group_3.model.Session;
import group_3.model.SessionStatistics;
import group_3.model.Ticket;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;

/**
 * Unit tests for EventStatisticsServiceImpl.
 * Uses Mockito to mock DAO dependencies.
 * 
 * @author Group 3
 */
@ExtendWith(MockitoExtension.class)
class EventStatisticsServiceTest {
    
    @Mock
    private EventDAO eventDAO;
    
    @Mock
    private SessionDAO sessionDAO;
    
    @Mock
    private TicketDAO ticketDAO;
    
    private EventStatisticsServiceImpl statsService;
    
    @BeforeEach
    void setUp() {
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
