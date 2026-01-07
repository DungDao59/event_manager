package group_3.dao;

import group_3.model.Session;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Group 3
 *
 * Data Access Object interface for Session entity.
 * Provides CRUD operations for sessions.
 */
public interface SessionDAO {

    /**
     * Create a new session in the database.
     * @param session the session to create
     */
    void create(Session session);

    /**
     * Retrieve a session by its ID.
     * @param sessionId the session identifier
     * @return an Optional containing the session if found
     */
    Optional<Session> findById(int sessionId);

    /**
     * Retrieve a session by title.
     * @param title the session title
     * @return an Optional containing the session if found
     */
    Optional<Session> findByTitle(String title);

    /**
     * Retrieve all sessions.
     * @return a list of all sessions
     */
    List<Session> findAll();

    /**
     * Retrieve all sessions belonging to a specific event.
     * @param eventId the event identifier
     * @return a list of sessions for the given event
     */
    List<Session> findByEventId(int eventId);

    /**
     * Update an existing session.
     * @param session the session with updated information
     */
    void update(Session session);

    /**
     * Delete a session by its ID.
     * @param sessionId the session identifier
     */
    void delete(int sessionId);

    /**
     * Get the count of all sessions.
     * @return the total number of sessions
     */
    int count();

    /**
     * Check if a session exists by ID.
     * @param sessionId the session identifier
     * @return true if the session exists, false otherwise
     */
    boolean exists(int sessionId);

    /**
     * Get the count of sessions for a specific event.
     * @param eventId the event identifier
     * @return the number of sessions in the event
     */
    int countByEventId(int eventId);
}
