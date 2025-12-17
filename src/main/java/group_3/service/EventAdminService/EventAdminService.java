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

    // ==================== SESSION OPERATIONS ====================

    /**
     * Create a new session for an event.
     * @param session the session to create
     * @return the created session with assigned ID
     */
    Session createSession(Session session);

    /**
     * Retrieve a session by ID.
     * @param sessionId the session identifier
     * @return an Optional containing the session if found
     */
    Optional<Session> getSessionById(int sessionId);

    /**
     * Retrieve a session by title.
     * @param title the session title
     * @return an Optional containing the session if found
     */
    Optional<Session> getSessionByTitle(String title);

    /**
     * Retrieve all sessions.
     * @return a list of all sessions
     */
    List<Session> getAllSessions();

    /**
     * Retrieve all sessions for a specific event.
     * @param eventId the event identifier
     * @return a list of sessions in the event
     */
    List<Session> getSessionsByEventId(int eventId);

    /**
     * Update an existing session.
     * @param session the session with updated information
     */
    void updateSession(Session session);

    /**
     * Delete a session by ID.
     * @param sessionId the session identifier
     */
    void deleteSession(int sessionId);

    /**
     * Get the total number of sessions.
     * @return the count of sessions
     */
    int getSessionCount();

    /**
     * Get the number of sessions for a specific event.
     * @param eventId the event identifier
     * @return the count of sessions in the event
     */
    int getSessionCountByEvent(int eventId);

    // ==================== COMBINED OPERATIONS ====================

    /**
     * Add a session to an event.
     * @param eventId the event identifier
     * @param sessionId the session identifier
     */
    void addSessionToEvent(int eventId, int sessionId);

    /**
     * Remove a session from an event.
     * @param eventId the event identifier
     * @param sessionId the session identifier
     */
    void removeSessionFromEvent(int eventId, int sessionId);

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

    /**
     * Check if a session exists.
     * @param sessionId the session identifier
     * @return true if the session exists, false otherwise
     */
    boolean sessionExists(int sessionId);
}
