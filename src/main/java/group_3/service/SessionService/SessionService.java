package group_3.service.SessionService;

import group_3.model.Session;
import java.util.*;

public interface SessionService {
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
     * Check if a session exists.
     * @param sessionId the session identifier
     * @return true if the session exists, false otherwise
     */
    boolean sessionExists(int sessionId);
}
