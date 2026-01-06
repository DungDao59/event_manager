package group_3.dao;

import group_3.model.SessionMaterial;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for SessionMaterial entity.
 * Provides CRUD operations for session materials.
 * 
 * @author Group 3
 */
public interface SessionMaterialDAO {

    /**
     * Create a new session material in the database.
     * @param material the session material to create
     */
    void create(SessionMaterial material);

    /**
     * Retrieve a session material by its ID.
     * @param materialId the material identifier
     * @return an Optional containing the material if found
     */
    Optional<SessionMaterial> findById(int materialId);

    /**
     * Retrieve all session materials.
     * @return a list of all session materials
     */
    List<SessionMaterial> findAll();

    /**
     * Retrieve all materials belonging to a specific session.
     * @param sessionId the session identifier
     * @return a list of materials for the given session
     */
    List<SessionMaterial> findBySessionId(int sessionId);

    /**
     * Retrieve materials by file type.
     * @param fileType the file type (e.g., "pdf", "video", "slides")
     * @return a list of materials with the given file type
     */
    List<SessionMaterial> findByFileType(String fileType);

    /**
     * Update an existing session material.
     * @param material the material with updated information
     */
    void update(SessionMaterial material);

    /**
     * Delete a session material by its ID.
     * @param materialId the material identifier
     */
    void delete(int materialId);

    /**
     * Delete all materials belonging to a specific session.
     * @param sessionId the session identifier
     */
    void deleteBySessionId(int sessionId);

    /**
     * Count materials for a specific session.
     * @param sessionId the session identifier
     * @return the number of materials for the session
     */
    int countBySessionId(int sessionId);
}
