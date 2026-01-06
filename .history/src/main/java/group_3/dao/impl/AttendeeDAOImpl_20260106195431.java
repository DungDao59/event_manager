package group_3.dao.impl;

import group_3.dao.AttendeeDAO;
import group_3.model.Attendee;
import group_3.model.enums.Role;
import group_3.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of AttendeeDAO for database operations.
 * Handles both person and attendee table operations.
 * 
 * @author Group21
 */
public class AttendeeDAOImpl implements AttendeeDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public int create(Attendee attendee) {
        String personSql = "INSERT INTO person (username, password_hash, full_name, date_of_birth, contact_information, role) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        String attendeeSql = "INSERT INTO attendee (person_id, history) VALUES (?, ?)";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                int personId;
                
                // Insert into person table
                try (PreparedStatement ps = conn.prepareStatement(personSql)) {
                    ps.setString(1, attendee.getUsername());
                    ps.setString(2, attendee.getPasswordHash());
                    ps.setString(3, attendee.getFullName());
                    ps.setDate(4, attendee.getDateOfBirth() != null ? java.sql.Date.valueOf(attendee.getDateOfBirth()) : null);
                    ps.setString(5, attendee.getContactInformation());
                    ps.setString(6, Role.ATTENDEE.name());
                    
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        personId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to create person record");
                    }
                }
                
                // Insert into attendee table
                try (PreparedStatement ps = conn.prepareStatement(attendeeSql)) {
                    ps.setInt(1, personId);
                    ps.setString(2, attendee.getHistory());
                    ps.executeUpdate();
                }
                
                conn.commit();
                return personId;
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating attendee", e);
        }
    }

    @Override
    public Optional<Attendee> findById(int personId) {
        String sql = "SELECT p.*, a.history FROM person p " +
                     "JOIN attendee a ON p.id = a.person_id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToAttendee(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding attendee by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Attendee> findByUsername(String username) {
        String sql = "SELECT p.*, a.history FROM person p " +
                     "JOIN attendee a ON p.id = a.person_id " +
                     "WHERE p.username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToAttendee(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding attendee by username", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Attendee> findAll() {
        String sql = "SELECT p.*, a.history FROM person p " +
                     "JOIN attendee a ON p.id = a.person_id";
        List<Attendee> attendees = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                attendees.add(mapRowToAttendee(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all attendees", e);
        }
        return attendees;
    }

    @Override
    public void update(Attendee attendee) {
        String personSql = "UPDATE person SET username = ?, password_hash = ?, full_name = ?, date_of_birth = ?, contact_information = ? WHERE id = ?";
        String attendeeSql = "UPDATE attendee SET history = ? WHERE person_id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Update person table
                try (PreparedStatement ps = conn.prepareStatement(personSql)) {
                    ps.setString(1, attendee.getUsername());
                    ps.setString(2, attendee.getPasswordHash());
                    ps.setString(3, attendee.getFullName());
                    ps.setDate(4, attendee.getDateOfBirth() != null ? java.sql.Date.valueOf(attendee.getDateOfBirth()) : null);
                    ps.setString(5, attendee.getContactInformation());
                    ps.setInt(6, attendee.getId());
                    ps.executeUpdate();
                }
                
                // Update attendee table
                try (PreparedStatement ps = conn.prepareStatement(attendeeSql)) {
                    ps.setString(1, attendee.getHistory());
                    ps.setInt(2, attendee.getId());
                    ps.executeUpdate();
                }
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating attendee", e);
        }
    }

    @Override
    public void delete(int personId) {
        String sql = "DELETE FROM person WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting attendee", e);
        }
    }

    @Override
    public void updateHistory(int personId, String history) {
        String sql = "UPDATE attendee SET history = ? WHERE person_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, history);
            ps.setInt(2, personId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating attendee history", e);
        }
    }

    private Attendee mapRowToAttendee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        LocalDate dob = dobDate != null ? dobDate.toLocalDate() : null;
        String contact = rs.getString("contact_information");
        String history = rs.getString("history");
        
        return new Attendee(id, username, passwordHash, fullName, dob, contact, history);
    }
}
