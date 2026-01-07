package group_3.dao;

import group_3.model.Presenter;
import java.util.List;
import java.util.Optional;

/**
 * @author Group 3
 *
 * Data Access Object interface for Presenter entity.
 * Handles database operations for the presenter table.
 */
public interface PresenterDAO {
    
    /**
     * Create a new presenter in the database.
     * This inserts into both person and presenter tables.
     * @param presenter the presenter to create
     * @return the ID of the created presenter
     */
    int create(Presenter presenter);
    
    /**
     * Find a presenter by their person ID.
     * @param personId the person ID
     * @return Optional containing the Presenter if found
     */
    Optional<Presenter> findById(int personId);
    
    /**
     * Find a presenter by username.
     * @param username the username
     * @return Optional containing the Presenter if found
     */
    Optional<Presenter> findByUsername(String username);
    
    /**
     * Retrieve all presenters.
     * @return list of all presenters
     */
    List<Presenter> findAll();
    
    /**
     * Update an existing presenter.
     * Updates both person and presenter tables.
     * @param presenter the presenter with updated information
     */
    void update(Presenter presenter);
    
    /**
     * Delete a presenter by person ID.
     * Cascades to delete from presenter table due to FK constraint.
     * @param personId the person ID to delete
     */
    void delete(int personId);
    
    /**
     * Update presenter's statistics.
     * @param personId the person ID
     * @param statistics the statistics JSON
     */
    void updateStatistics(int personId, String statistics);
    
    /**
     * Update presenter's role.
     * @param personId the person ID
     * @param presenterRole the presenter role
     */
    void updatePresenterRole(int personId, String presenterRole);
}
