package group_3.service.EventStatisticsService;

/**
 * Quick Reference Guide for Event Statistics Service
 * 
 * This class serves as documentation for the statistics calculation formulas.
 * 
 * @author Group 3
 */
public class StatisticsFormulas {
    
    /*
     * =====================================================================
     * REVENUE CALCULATIONS
     * =====================================================================
     */
    
    /**
     * Event Revenue Formula:
     * 
     * Total Revenue = Sum of (ticket.price) 
     *                 WHERE ticket.eventID = eventId
     *                   AND ticket.status IN ('ACTIVE', 'USED')
     * 
     * Explanation:
     * - Only counts tickets that have been sold (ACTIVE) or used (USED)
     * - Excludes CANCELLED tickets (refunded or invalid)
     * - Sums the price field from all matching tickets
     */
    
    /*
     * =====================================================================
     * ATTENDANCE CALCULATIONS
     * =====================================================================
     */
    
    /**
     * Tickets Sold Formula:
     * 
     * Tickets Sold = Count of tickets
     *                WHERE ticket.eventID = eventId
     *                  AND ticket.status IN ('ACTIVE', 'USED')
     * 
     * Explanation:
     * - Represents total number of valid tickets
     * - Includes both unused (ACTIVE) and checked-in (USED) tickets
     * - Does not include CANCELLED tickets
     */
    
    /**
     * Checked-In Attendees Formula:
     * 
     * Checked In = Count of tickets
     *              WHERE ticket.eventID = eventId
     *                AND ticket.status = 'USED'
     * 
     * Explanation:
     * - Only counts tickets that have been scanned/used at the event
     * - Represents actual attendance
     * - USED status is set when attendee checks in
     */
    
    /**
     * Attendance Rate Formula:
     * 
     * Attendance Rate = (Checked In / Tickets Sold) × 100
     * 
     * Where:
     * - Checked In = tickets with status 'USED'
     * - Tickets Sold = tickets with status 'ACTIVE' or 'USED'
     * 
     * Result: Percentage (0-100)
     * 
     * Examples:
     * - 50 checked in, 100 sold → 50.0%
     * - 80 checked in, 100 sold → 80.0%
     * - 0 checked in, 0 sold → 0.0% (no division by zero)
     */
    
    /*
     * =====================================================================
     * SESSION POPULARITY CALCULATIONS
     * =====================================================================
     */
    
    /**
     * Session Popularity Ranking:
     * 
     * Popularity = Total Tickets Sold for Session
     * 
     * Sessions are ranked by:
     * 1. Total tickets sold (primary metric)
     * 2. Higher sales = more popular
     * 
     * Formula:
     * For each session:
     *   Tickets Sold = Count of tickets
     *                  WHERE ticket.sessionID = sessionId
     *                    AND ticket.status IN ('ACTIVE', 'USED')
     * 
     * Then sort sessions by Tickets Sold (descending)
     */
    
    /**
     * Capacity Utilization Formula:
     * 
     * Capacity Utilization = (Tickets Sold / Session Capacity) × 100
     * 
     * Where:
     * - Tickets Sold = tickets for this session (ACTIVE or USED)
     * - Session Capacity = session.capacity (max attendees)
     * 
     * Result: Percentage (0-100+)
     * Note: Can exceed 100% if overbooking occurred
     * 
     * Examples:
     * - 80 sold, 100 capacity → 80.0%
     * - 100 sold, 100 capacity → 100.0%
     * - 120 sold, 100 capacity → 120.0% (overbooked)
     */
    
    /*
     * =====================================================================
     * TICKET STATUS DEFINITIONS
     * =====================================================================
     */
    
    /**
     * ACTIVE:
     * - Ticket has been purchased/issued
     * - Ticket is valid and not yet used
     * - Attendee has not checked in yet
     * 
     * USED:
     * - Ticket has been scanned/validated at event
     * - Attendee has checked in
     * - Counts towards attendance metrics
     * 
     * CANCELLED:
     * - Ticket has been refunded or invalidated
     * - Does not count in revenue or attendance
     * - Excluded from all statistics
     */
    
    /*
     * =====================================================================
     * CALCULATION EXAMPLES
     * =====================================================================
     */
    
    /**
     * Example Event Statistics:
     * 
     * Event: "Tech Conference 2025"
     * 
     * Tickets:
     * - Ticket #1: $100, ACTIVE  (event=1)
     * - Ticket #2: $150, USED    (event=1)
     * - Ticket #3: $200, USED    (event=1)
     * - Ticket #4: $100, CANCELLED (event=1)
     * - Ticket #5: $75,  ACTIVE  (event=2)
     * 
     * Calculations for Event 1:
     * 
     * Revenue = $100 + $150 + $200 = $450
     * (Excludes CANCELLED and other events)
     * 
     * Tickets Sold = 3 (ACTIVE + USED count)
     * (IDs: 1, 2, 3)
     * 
     * Checked In = 2 (USED count)
     * (IDs: 2, 3)
     * 
     * Attendance Rate = (2 / 3) × 100 = 66.67%
     */
    
    /**
     * Example Session Statistics:
     * 
     * Session: "AI Workshop"
     * Capacity: 50
     * 
     * Tickets:
     * - 30 tickets with status ACTIVE
     * - 15 tickets with status USED
     * - 5 tickets with status CANCELLED
     * 
     * Calculations:
     * 
     * Tickets Sold = 30 + 15 = 45
     * (Excludes CANCELLED)
     * 
     * Checked In = 15
     * 
     * Attendance Rate = (15 / 45) × 100 = 33.33%
     * 
     * Capacity Utilization = (45 / 50) × 100 = 90.0%
     */
    
    /*
     * =====================================================================
     * EDGE CASES
     * =====================================================================
     */
    
    /**
     * No Tickets:
     * - Revenue = $0.00
     * - Tickets Sold = 0
     * - Checked In = 0
     * - Attendance Rate = 0.0% (not NaN)
     * 
     * All Cancelled:
     * - Revenue = $0.00
     * - Tickets Sold = 0
     * - Checked In = 0
     * - Attendance Rate = 0.0%
     * 
     * Zero Capacity Session:
     * - Capacity Utilization = 0.0% (not NaN or infinity)
     */
}
