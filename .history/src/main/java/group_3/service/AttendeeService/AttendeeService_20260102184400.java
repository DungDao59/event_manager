package group_3.service.AttendeeService;

import java.util.List;
import java.util.Optional;

import group_3.model.Attendee;

/**
 * AttendeeService provides operations for managing Attendees.
 * Handles CRUD operations and personal information updates for Attendees.
 * 
 * @author Group21
 */
public interface AttendeeService {
    
    // Create operations
    /**
     * Create a new Attendee
     * @param attendee the Attendee to create
     * @return the created Attendee with assigned ID
     */
    Attendee createAttendee(Attendee attendee);
    
    // Read operations
    /**
     * Get an Attendee by ID
     * @param attendeeId the Attendee ID
     * @return Optional containing the Attendee if found
     */
    Optional<Attendee> getAttendeeById(int attendeeId);
    
    /**
     * Get an Attendee by username
     * @param username the username
     * @return Optional containing the Attendee if found
     */
    Optional<Attendee> getAttendeeByUsername(String username);
    
    /**
     * Get all Attendees
     * @return List of all Attendees
     */
    List<Attendee> getAllAttendees();
    
    // Update operations - Personal Information
    /**
     * Update Attendee's history
     * @param attendeeId the Attendee ID
     * @param history new history (JSON)
     */
    void updateHistory(int attendeeId, String history);
    
    /**
     * Update the complete Attendee information
     * @param attendee the Attendee with updated information
     */
    void updateAttendee(Attendee attendee);
    
    // Delete operations
    /**
     * Delete an Attendee by ID
     * @param attendeeId the Attendee ID to delete
     */
    void deleteAttendee(int attendeeId);
    
    // Utility operations
    /**
     * Check if an Attendee exists by ID
     * @param attendeeId the Attendee ID
     * @return true if Attendee exists, false otherwise
     */
    boolean attendeeExists(int attendeeId);
    
    /**
     * Get total count of Attendees
     * @return total Attendee count
     */
    int getTotalAttendeeCount();
}
