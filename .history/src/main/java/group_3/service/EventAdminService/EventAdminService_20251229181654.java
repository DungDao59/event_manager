package group_3.service.EventAdminService;

import group_3.model.Event;
import group_3.model.Session;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Event Administration.
 * Provides business logic operations for managing events and sessions.
 * 
 * Author: Group 3
 */
public interface EventAdminService {

    // ==================== EVENT OPERATIONS ====================

    /**
     * Create a new event.
     * @param event the event to create
     * @return the created event with assigned ID
     */
    Event createEvent(Event event);

    /**
     * Retrieve an event by ID.
     * @param eventId the event identifier
     * @return an Optional containing the event if found
     */
    Optional<Event> getEventById(int eventId);

    /**
     * Retrieve an event by name.
     * @param name the event name
     * @return an Optional containing the event if found
     */
    Optional<Event> getEventByName(String name);

    /**
     * Retrieve all events.
     * @return a list of all events
     */
    List<Event> getAllEvents();

    /**
     * Update an existing event.
     * @param event the event with updated information
     */
    void updateEvent(Event event);

    /**
     * Delete an event by ID.
     * @param eventId the event identifier
     */
    void deleteEvent(int eventId);

    /**
     * Get the total number of events.
     * @return the count of events
     */
    int getEventCount();

    // ==================== COMBINED OPERATIONS ====================
    /**
     * Get all details of an event including its sessions.
     * @param eventId the event identifier
     * @return an Optional containing a map with event and its sessions
     */
    Optional<Event> getEventWithSessions(int eventId);

    /**
     * Check if an event exists.
     * @param eventId the event identifier
     * @return true if the event exists, false otherwise
     */
    boolean eventExists(int eventId);
}
