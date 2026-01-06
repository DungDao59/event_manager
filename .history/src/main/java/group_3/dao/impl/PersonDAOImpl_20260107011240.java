package group_3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.PersonDAO;
import group_3.model.Admin;
import group_3.model.Attendee;
import group_3.model.Person;
import group_3.model.Presenter;
import group_3.model.enums.Role;
import group_3.util.DatabaseConnection;

public class PersonDAOImpl implements PersonDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        String sql = "SELECT * FROM person WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToPerson(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password");
        String fullName = rs.getString("full_name");
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        LocalDate dob = (dobDate != null) ? dobDate.toLocalDate() : null;
        String contact = rs.getString("contact_information");
        Role role = Role.valueOf(rs.getString("role"));

        if (role == Role.ATTENDEE) {
            return new Attendee(id, username, passwordHash, fullName, dob, contact, null);
        } else if (role == Role.PRESENTER) {
            return new Presenter(id, username, passwordHash, fullName, dob, contact, null, null);
        } else if (role == Role.EVENT_ADMIN || role == Role.SYSTEM_ADMIN) {
            return new Admin(id, username, passwordHash, fullName, dob, contact, role);
        }
        // Fallback: create Admin with the given role
        return new Admin(id, username, passwordHash, fullName, dob, contact, role);
    }

    @Override
    public void create(Person person) {
        String sql = "INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES (?, ?, ?, ?, ?::jsonb, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, person.getUsername());
            ps.setString(2, person.getPasswordHash());
            ps.setString(3, person.getFullName());
            ps.setDate(4, java.sql.Date.valueOf(person.getDateOfBirth()));
            // Wrap contact info in JSON format if it's not already JSON
            String contactInfo = person.getContactInformation();
            if (contactInfo != null && !contactInfo.startsWith("{")) {
                contactInfo = "{\"email\":\"" + contactInfo + "\"}";
            }
            ps.setString(5, contactInfo);
            ps.setString(6, person.getRole().name());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Person> findById(int id) {
        String sql = "SELECT * FROM person WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToPerson(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        String sql = "SELECT * FROM person";
        List<Person> persons = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                persons.add(mapRowToPerson(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    @Override
    public void update(Person person) {
        String sql = "UPDATE person SET username = ?, password = ?, full_name = ?, date_of_birth = ?, contact_information = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, person.getUsername());
            ps.setString(2, person.getPasswordHash());
            ps.setString(3, person.getFullName());
            ps.setDate(4, java.sql.Date.valueOf(person.getDateOfBirth()));
            ps.setString(5, person.getContactInformation());
            ps.setString(6, person.getRole().name());
            ps.setInt(7, person.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM person WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
