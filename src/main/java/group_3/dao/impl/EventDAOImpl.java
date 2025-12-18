package group_3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.EventDAO;
import group_3.model.Event;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.util.DatabaseConnection;
/**
 * Implementation of EventDAO interface.
 * Handles database operations for Event entities.
 * 
 * Author: Group 3
 */
public class EventDAOImpl implements EventDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void create(Event event) {
        String sql = "INSERT INTO event (name, type, start_date, end_date, location, duration, status, event_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, event.getName());
            ps.setString(2, event.getType() != null ? event.getType().name() : null);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(event.getEndDate()));
            ps.setString(5, event.getLocation());
            ps.setInt(6, event.getDuration());
            ps.setString(7, event.getStatus() != null ? event.getStatus().name() : "SCHEDULED");
            ps.setString(8, event.getEventImage());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                event.setEventId(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating event: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Event> findById(int eventId) {
        String sql = "SELECT * FROM event WHERE event_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToEvent(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding event by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Event> findByName(String name) {
        String sql = "SELECT * FROM event WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToEvent(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding event by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT * FROM event";
        List<Event> events = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all events: " + e.getMessage(), e);
        }
        return events;
    }

    @Override
    public void update(Event event) {
        String sql = "UPDATE event SET name = ?, type = ?, start_date = ?, end_date = ?, location = ?, duration = ?, status = ?, event_image = ? WHERE event_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getName());
            ps.setString(2, event.getType() != null ? event.getType().name() : null);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(event.getEndDate()));
            ps.setString(5, event.getLocation());
            ps.setInt(6, event.getDuration());
            ps.setString(7, event.getStatus() != null ? event.getStatus().name() : "SCHEDULED");
            ps.setString(8, event.getEventImage());
            ps.setInt(9, Integer.parseInt(event.getEventId()));

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating event: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int eventId) {
        String sql = "DELETE FROM event WHERE event_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting event: " + e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM event";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error counting events: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean exists(int eventId) {
        String sql = "SELECT 1 FROM event WHERE event_id = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException("Error checking event existence: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a database result row to an Event object.
     * @param rs the result set row
     * @return an Event object
     * @throws SQLException if there's a SQL error
     */
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        int id = rs.getInt("event_id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");
        java.sql.Timestamp startDateSql = rs.getTimestamp("start_date");
        java.sql.Timestamp endDateSql = rs.getTimestamp("end_date");
        String location = rs.getString("location");
        int duration = rs.getInt("duration");
        String statusStr = rs.getString("status");
        String eventImage = rs.getString("event_image");

        EventType type = null;
        if (typeStr != null) {
            try {
                type = EventType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                type = EventType.CONFERENCE;
            }
        }

        EventStatus status = EventStatus.SCHEDULED;
        if (statusStr != null) {
            try {
                status = EventStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                status = EventStatus.SCHEDULED;
            }
        }

        LocalDateTime startDate = startDateSql != null ? startDateSql.toLocalDateTime() : LocalDateTime.now();
        LocalDateTime endDate = endDateSql != null ? endDateSql.toLocalDateTime() : startDate.plusDays(duration);

        Event event = new Event(String.valueOf(id), name, type, startDate, endDate, location, duration, status, eventImage);
        return event;
    }
}
