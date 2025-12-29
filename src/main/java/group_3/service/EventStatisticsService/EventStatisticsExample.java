package group_3.service.EventStatisticsService;

import group_3.dao.EventDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.EventDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.dao.impl.TicketDAOImpl;
import group_3.model.EventStatistics;
import group_3.model.SessionStatistics;

import java.util.List;
import java.util.Optional;

/**
 * Example usage of EventStatisticsService.
 * Demonstrates how to calculate revenue, attendance rates, and identify popular sessions.
 * 
 * @author Group 3
 */
public class EventStatisticsExample {
    
    public static void main(String[] args) {
        // Initialize DAOs
        EventDAO eventDAO = new EventDAOImpl();
        SessionDAO sessionDAO = new SessionDAOImpl();
        TicketDAO ticketDAO = new TicketDAOImpl();
        
        // Create service instance
        EventStatisticsService statsService = new EventStatisticsServiceImpl(eventDAO, sessionDAO, ticketDAO);
        
        // Example 1: Get statistics for a specific event
        System.out.println("=== Example 1: Event Statistics ===");
        int eventId = 1;
        Optional<EventStatistics> eventStats = statsService.getEventStatistics(eventId);
        
        if (eventStats.isPresent()) {
            EventStatistics stats = eventStats.get();
            System.out.println("Event: " + stats.getEventName());
            System.out.println("Total Revenue: $" + String.format("%.2f", stats.getTotalRevenue()));
            System.out.println("Tickets Sold: " + stats.getTotalTicketsSold());
            System.out.println("Checked In: " + stats.getTotalCheckedIn());
            System.out.println("Attendance Rate: " + String.format("%.2f", stats.getAttendanceRate()) + "%");
        } else {
            System.out.println("Event not found.");
        }
        
        // Example 2: Calculate revenue only
        System.out.println("\n=== Example 2: Calculate Event Revenue ===");
        double revenue = statsService.calculateEventRevenue(eventId);
        System.out.println("Event " + eventId + " Revenue: $" + String.format("%.2f", revenue));
        
        // Example 3: Calculate attendance rate
        System.out.println("\n=== Example 3: Calculate Attendance Rate ===");
        double attendanceRate = statsService.calculateEventAttendanceRate(eventId);
        System.out.println("Event " + eventId + " Attendance Rate: " + String.format("%.2f", attendanceRate) + "%");
        
        // Example 4: Get most popular sessions for an event
        System.out.println("\n=== Example 4: Most Popular Sessions (Top 5) ===");
        List<SessionStatistics> popularSessions = statsService.getMostPopularSessions(eventId, 5);
        
        if (popularSessions.isEmpty()) {
            System.out.println("No sessions found for this event.");
        } else {
            for (int i = 0; i < popularSessions.size(); i++) {
                SessionStatistics session = popularSessions.get(i);
                System.out.println((i + 1) + ". " + session.getSessionTitle());
                System.out.println("   Tickets Sold: " + session.getTotalTicketsSold());
                System.out.println("   Checked In: " + session.getTotalCheckedIn());
                System.out.println("   Attendance Rate: " + String.format("%.2f", session.getAttendanceRate()) + "%");
                System.out.println("   Capacity Utilization: " + String.format("%.2f", session.getCapacityUtilization()) + "%");
            }
        }
        
        // Example 5: Get session statistics for all sessions in an event
        System.out.println("\n=== Example 5: All Session Statistics for Event ===");
        List<SessionStatistics> allSessionStats = statsService.getEventSessionStatistics(eventId);
        System.out.println("Total Sessions: " + allSessionStats.size());
        
        for (SessionStatistics session : allSessionStats) {
            System.out.println("- " + session.getSessionTitle() + ": " + 
                             session.getTotalTicketsSold() + " tickets sold");
        }
        
        // Example 6: Get most popular sessions globally (across all events)
        System.out.println("\n=== Example 6: Most Popular Sessions Globally (Top 3) ===");
        List<SessionStatistics> globalPopular = statsService.getMostPopularSessionsGlobal(3);
        
        for (int i = 0; i < globalPopular.size(); i++) {
            SessionStatistics session = globalPopular.get(i);
            System.out.println((i + 1) + ". " + session.getSessionTitle());
            System.out.println("   Event ID: " + session.getEventId());
            System.out.println("   Tickets Sold: " + session.getTotalTicketsSold());
        }
        
        // Example 7: Get statistics for all events
        System.out.println("\n=== Example 7: Statistics for All Events ===");
        List<EventStatistics> allEventStats = statsService.getAllEventStatistics();
        
        if (allEventStats.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("Total Events: " + allEventStats.size());
            System.out.println("\nEvent Summary:");
            
            double totalRevenue = 0;
            int totalTickets = 0;
            
            for (EventStatistics stats : allEventStats) {
                System.out.println("- " + stats.getEventName());
                System.out.println("  Revenue: $" + String.format("%.2f", stats.getTotalRevenue()));
                System.out.println("  Attendance Rate: " + String.format("%.2f", stats.getAttendanceRate()) + "%");
                
                totalRevenue += stats.getTotalRevenue();
                totalTickets += stats.getTotalTicketsSold();
            }
            
            System.out.println("\nOverall Statistics:");
            System.out.println("Total Revenue: $" + String.format("%.2f", totalRevenue));
            System.out.println("Total Tickets Sold: " + totalTickets);
        }
    }
}
