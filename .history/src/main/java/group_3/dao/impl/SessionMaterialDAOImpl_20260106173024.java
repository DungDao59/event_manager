package group_3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.SessionMaterialDAO;
import group_3.model.SessionMaterial;
import group_3.util.DatabaseConnection;

/**
 * Implementation of SessionMaterialDAO interface.
 * Handles database operations for SessionMaterial entities.
 * 
 * @author Group 3
 */
public class SessionMaterialDAOImpl implements SessionMaterialDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void create(SessionMaterial material) {
        String sql = "INSERT INTO session_material (session_id, title, description, file_type, content_data, content_url) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, material.getSessionId());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getDescription());
            ps.setString(4, material.getFileType());
            ps.setBytes(5, material.getContentData());
            ps.setString(6, material.getContentUrl());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                material.setMaterialId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating session material: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SessionMaterial> findById(int materialId) {
        String sql = "SELECT * FROM session_material WHERE material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToSessionMaterial(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding session material by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<SessionMaterial> findAll() {
        String sql = "SELECT * FROM session_material";
        List<SessionMaterial> materials = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                materials.add(mapRowToSessionMaterial(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all session materials: " + e.getMessage(), e);
        }
        return materials;
    }

    @Override
    public List<SessionMaterial> findBySessionId(int sessionId) {
        String sql = "SELECT * FROM session_material WHERE session_id = ?";
        List<SessionMaterial> materials = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapRowToSessionMaterial(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding session materials by session ID: " + e.getMessage(), e);
        }
        return materials;
    }

    @Override
    public List<SessionMaterial> findByFileType(String fileType) {
        String sql = "SELECT * FROM session_material WHERE file_type = ?";
        List<SessionMaterial> materials = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapRowToSessionMaterial(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding session materials by file type: " + e.getMessage(), e);
        }
        return materials;
    }

    @Override
    public void update(SessionMaterial material) {
        String sql = "UPDATE session_material SET session_id = ?, title = ?, description = ?, " +
                "file_type = ?, content_data = ?, content_url = ? WHERE material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, material.getSessionId());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getDescription());
            ps.setString(4, material.getFileType());
            ps.setBytes(5, material.getContentData());
            ps.setString(6, material.getContentUrl());
            ps.setInt(7, material.getMaterialId());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating session material: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int materialId) {
        String sql = "DELETE FROM session_material WHERE material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting session material: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBySessionId(int sessionId) {
        String sql = "DELETE FROM session_material WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting session materials by session ID: " + e.getMessage(), e);
        }
    }

    @Override
    public int countBySessionId(int sessionId) {
        String sql = "SELECT COUNT(*) FROM session_material WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error counting session materials: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Maps a database row to a SessionMaterial object.
     * @param rs the ResultSet positioned at the current row
     * @return a SessionMaterial object
     * @throws SQLException if a database access error occurs
     */
    private SessionMaterial mapRowToSessionMaterial(ResultSet rs) throws SQLException {
        return new SessionMaterial(
            rs.getInt("material_id"),
            rs.getInt("session_id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("file_type"),
            rs.getBytes("content_data"),
            rs.getString("content_url")
        );
    }
}
