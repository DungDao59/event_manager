package group_3.dao.impl;

import group_3.model.Session;
import group_3.dao.SessionDAO;
import group_3.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of SessionDAO interface.
 * Handles database operations for Session entities.
 * 
 * Author: Group 3
 */
public class SessionDAOImpl implements SessionDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void create(Session session) {
        String sql = "INSERT INTO session (event_id, title, description, scheduled_date, start_time, end_time, venue, capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime scheduledDateTime = session.getScheduledDateTime();
            
            ps.setInt(1, extractEventIdFromSessionId(session.getSessionId()));
            ps.setString(2, session.getTitle());
            ps.setString(3, session.getDescription());
            ps.setDate(4, java.sql.Date.valueOf(scheduledDateTime.toLocalDate()));
            ps.setTime(5, java.sql.Time.valueOf(scheduledDateTime.toLocalTime()));
            ps.setTime(6, java.sql.Time.valueOf(scheduledDateTime.toLocalTime().plusHours(1))); // Default 1 hour duration
            ps.setString(7, session.getVenue());
            ps.setInt(8, session.getCapacity());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                session.setSessionId(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating session: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Session> findById(int sessionId) {
        String sql = "SELECT * FROM session WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToSession(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding session by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Session> findByTitle(String title) {
        String sql = "SELECT * FROM session WHERE title = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToSession(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding session by title: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Session> findAll() {
        String sql = "SELECT * FROM session";
        List<Session> sessions = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all sessions: " + e.getMessage(), e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByEventId(int eventId) {
        String sql = "SELECT * FROM session WHERE event_id = ?";
        List<Session> sessions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding sessions by event ID: " + e.getMessage(), e);
        }
        return sessions;
    }

    @Override
    public void update(Session session) {
        String sql = "UPDATE session SET title = ?, description = ?, scheduled_date = ?, " +
                "start_time = ?, end_time = ?, venue = ?, capacity = ? WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDateTime scheduledDateTime = session.getScheduledDateTime();
            
            ps.setString(1, session.getTitle());
            ps.setString(2, session.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(scheduledDateTime.toLocalDate()));
            ps.setTime(4, java.sql.Time.valueOf(scheduledDateTime.toLocalTime()));
            ps.setTime(5, java.sql.Time.valueOf(scheduledDateTime.toLocalTime().plusHours(1)));
            ps.setString(6, session.getVenue());
            ps.setInt(7, session.getCapacity());
            ps.setInt(8, Integer.parseInt(session.getSessionId()));

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating session: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int sessionId) {
        String sql = "DELETE FROM session WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting session: " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM session";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error counting sessions: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean exists(int sessionId) {
        String sql = "SELECT 1 FROM session WHERE session_id = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException("Error checking session existence: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM session WHERE event_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error counting sessions by event: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Maps a database result row to a Session object.
     * @param rs the result set row
     * @return a Session object
     * @throws SQLException if there's a SQL error
     */
    private Session mapRowToSession(ResultSet rs) throws SQLException {
        int id = rs.getInt("session_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        java.sql.Date scheduledDate = rs.getDate("scheduled_date");
        Time startTime = rs.getTime("start_time");
        String venue = rs.getString("venue");
        int capacity = rs.getInt("capacity");

        LocalDateTime scheduledDateTime = LocalDateTime.of(
                scheduledDate.toLocalDate(),
                startTime.toLocalTime()
        );

        Session session = new Session(
                String.valueOf(id),
                title,
                description,
                scheduledDateTime,
                venue,
                capacity
        );
        return session;
    }

    /**
     * Helper method to extract event ID from session (placeholder implementation).
     * In a real scenario, this might require a database lookup or be passed differently.
     * @param sessionId the session ID
     * @return the event ID (default: 1 for now)
     */
    @SuppressWarnings("unused")
    private int extractEventIdFromSessionId(String sessionId) {
        // This is a simplified implementation - in production, you'd likely have
        // event_id available or need to query the database
        return 1;
    }
}
