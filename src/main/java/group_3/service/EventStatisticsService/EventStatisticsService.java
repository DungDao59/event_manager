package group_3.service.EventStatisticsService;

import java.util.List;
import java.util.Optional;

import group_3.model.EventStatistics;
import group_3.model.SessionStatistics;

/**
 * @author Group 3
 *
 * Service interface for Event Statistics operations.
 * Provides methods to calculate revenue, attendance rates, and popularity metrics.
 */
public interface EventStatisticsService {
    
    /**
     * Calculate comprehensive statistics for a specific event.
     * Includes total revenue, tickets sold, checked-in attendees, and attendance rate.
     * 
     * @param eventId the event identifier
     * @return an Optional containing EventStatistics if event exists, empty otherwise
     */
    Optional<EventStatistics> getEventStatistics(int eventId);
    
    /**
     * Calculate revenue for a specific event.
     * 
     * @param eventId the event identifier
     * @return the total revenue from all tickets sold for the event
     */
    double calculateEventRevenue(int eventId);
    
    /**
     * Calculate attendance rate for a specific event.
     * Returns the percentage of checked-in attendees vs total tickets sold.
     * 
     * @param eventId the event identifier
     * @return attendance rate as a percentage (0-100)
     */
    double calculateEventAttendanceRate(int eventId);
    
    /**
     * Get statistics for a specific session.
     * 
     * @param sessionId the session identifier
     * @return an Optional containing SessionStatistics if session exists, empty otherwise
     */
    Optional<SessionStatistics> getSessionStatistics(int sessionId);
    
    /**
     * Get statistics for all sessions within an event.
     * 
     * @param eventId the event identifier
     * @return a list of SessionStatistics for all sessions in the event
     */
    List<SessionStatistics> getEventSessionStatistics(int eventId);
    
    /**
     * Find the most popular sessions for an event based on ticket sales.
     * 
     * @param eventId the event identifier
     * @param limit the maximum number of sessions to return
     * @return a list of the top sessions ordered by ticket sales (descending)
     */
    List<SessionStatistics> getMostPopularSessions(int eventId, int limit);
    
    /**
     * Find the most popular sessions across all events based on ticket sales.
     * 
     * @param limit the maximum number of sessions to return
     * @return a list of the top sessions ordered by ticket sales (descending)
     */
    List<SessionStatistics> getMostPopularSessionsGlobal(int limit);
    
    /**
     * Get total number of tickets sold for an event.
     * 
     * @param eventId the event identifier
     * @return the count of tickets with status ACTIVE or USED
     */
    int getTotalTicketsSold(int eventId);
    
    /**
     * Get total number of checked-in attendees for an event.
     * 
     * @param eventId the event identifier
     * @return the count of tickets with status USED
     */
    int getTotalCheckedIn(int eventId);
    
    /**
     * Get statistics for all events in the system.
     * This is an optimized method that fetches all data once instead of per-event queries.
     * 
     * @return a list of EventStatistics for all events
     */
    List<EventStatistics> getAllEventStatistics();
}
