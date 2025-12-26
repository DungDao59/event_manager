package group_3.dao;

import group_3.model.Attendee;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Attendee entity.
 * Handles database operations for the attendee table.
 * 
 * @author Group21
 */
public interface AttendeeDAO {
    
    /**
     * Create a new attendee in the database.
     * This inserts into both person and attendee tables.
     * @param attendee the attendee to create
     * @return the ID of the created attendee
     */
    int create(Attendee attendee);
    
    /**
     * Find an attendee by their person ID.
     * @param personId the person ID
     * @return Optional containing the Attendee if found
     */
    Optional<Attendee> findById(int personId);
    
    /**
     * Find an attendee by username.
     * @param username the username
     * @return Optional containing the Attendee if found
     */
    Optional<Attendee> findByUsername(String username);
    
    /**
     * Retrieve all attendees.
     * @return list of all attendees
     */
    List<Attendee> findAll();
    
    /**
     * Update an existing attendee.
     * Updates both person and attendee tables.
     * @param attendee the attendee with updated information
     */
    void update(Attendee attendee);
    
    /**
     * Delete an attendee by person ID.
     * Cascades to delete from attendee table due to FK constraint.
     * @param personId the person ID to delete
     */
    void delete(int personId);
    
    /**
     * Update attendee's history.
     * @param personId the person ID
     * @param history the history JSON
     */
    void updateHistory(int personId, String history);
}
