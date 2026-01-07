package group_3.dao;

import group_3.model.Event;
import java.util.List;
import java.util.Optional;

/**
 * @author Group 3
 *
 * Data Access Object interface for Event entity.
 * Provides CRUD operations for events.
 */
public interface EventDAO {

    /**
     * Create a new event in the database.
     * @param event the event to create
     */
    void create(Event event);

    /**
     * Retrieve an event by its ID.
     * @param eventId the event identifier
     * @return an Optional containing the event if found
     */
    Optional<Event> findById(int eventId);

    /**
     * Retrieve an event by name.
     * @param name the event name
     * @return an Optional containing the event if found
     */
    Optional<Event> findByName(String name);

    /**
     * Retrieve all events.
     * @return a list of all events
     */
    List<Event> findAll();

    /**
     * Update an existing event.
     * @param event the event with updated information
     */
    void update(Event event);

    /**
     * Delete an event by its ID.
     * @param eventId the event identifier
     */
    void delete(int eventId);

    /**
     * Get the count of all events.
     * @return the total number of events
     */
    int count();

    /**
     * Check if an event exists by ID.
     * @param eventId the event identifier
     * @return true if the event exists, false otherwise
     */
    boolean exists(int eventId);
}
