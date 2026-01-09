package group_3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.PresenterDAO;
import group_3.model.Presenter;
import group_3.model.enums.Role;
import group_3.util.DatabaseConnection;

/**
 * @author Group 3
 *
 * Implementation of PresenterDAO for database operations.
 * Handles both person and presenter table operations.
 *
 */
public class PresenterDAOImpl implements PresenterDAO {

    private Connection getConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    @Override
    public int create(Presenter presenter) {
        String personSql = "INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES (?, ?, ?, ?, ?::jsonb, ?::user_role) RETURNING id";
        String presenterSql = "INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (?, ?, ?::jsonb)";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                int personId;
                
                // Insert into person table
                try (PreparedStatement ps = conn.prepareStatement(personSql)) {
                    ps.setString(1, presenter.getUsername());
                    ps.setString(2, presenter.getPasswordHash());
                    ps.setString(3, presenter.getFullName());
                    ps.setDate(4, presenter.getDateOfBirth() != null ? java.sql.Date.valueOf(presenter.getDateOfBirth()) : null);
                    ps.setString(5, presenter.getContactInformation());
                    ps.setString(6, Role.PRESENTER.name());
                    
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        personId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to create person record");
                    }
                }
                
                // Insert into presenter table
                try (PreparedStatement ps = conn.prepareStatement(presenterSql)) {
                    ps.setInt(1, personId);
                    ps.setString(2, presenter.getPresenterRole());
                    ps.setString(3, presenter.getStatistics());
                    ps.executeUpdate();
                }
                
                conn.commit();
                return personId;
                
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating presenter", e);
        }
    }

    @Override
    public Optional<Presenter> findById(int personId) {
        String sql = "SELECT p.*, pr.presenter_role, pr.statistics FROM person p " +
                     "JOIN presenter pr ON p.id = pr.person_id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToPresenter(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding presenter by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Presenter> findByUsername(String username) {
        String sql = "SELECT p.*, pr.presenter_role, pr.statistics FROM person p " +
                     "JOIN presenter pr ON p.id = pr.person_id " +
                     "WHERE p.username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToPresenter(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding presenter by username", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Presenter> findAll() {
        String sql = "SELECT p.*, pr.presenter_role, pr.statistics FROM person p " +
                     "JOIN presenter pr ON p.id = pr.person_id";
        List<Presenter> presenters = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                presenters.add(mapRowToPresenter(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding all presenters", e);
        }
        return presenters;
    }

    @Override
    public void update(Presenter presenter) {
        String personSql = "UPDATE person SET username = ?, password = ?, full_name = ?, date_of_birth = ?, contact_information = ?::jsonb WHERE id = ?";
        String presenterSql = "UPDATE presenter SET presenter_role = ?, statistics = ? WHERE person_id = ?";
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Update person table
                try (PreparedStatement ps = conn.prepareStatement(personSql)) {
                    ps.setString(1, presenter.getUsername());
                    ps.setString(2, presenter.getPasswordHash());
                    ps.setString(3, presenter.getFullName());
                    ps.setDate(4, presenter.getDateOfBirth() != null ? java.sql.Date.valueOf(presenter.getDateOfBirth()) : null);
                    ps.setString(5, presenter.getContactInformation());
                    ps.setInt(6, presenter.getId());
                    ps.executeUpdate();
                }
                
                // Update presenter table
                try (PreparedStatement ps = conn.prepareStatement(presenterSql)) {
                    ps.setString(1, presenter.getPresenterRole());
                    ps.setString(2, presenter.getStatistics());
                    ps.setInt(3, presenter.getId());
                    ps.executeUpdate();
                }
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating presenter", e);
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
            throw new RuntimeException("Error deleting presenter", e);
        }
    }

    @Override
    public void updateStatistics(int personId, String statistics) {
        String sql = "UPDATE presenter SET statistics = ? WHERE person_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statistics);
            ps.setInt(2, personId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating presenter statistics", e);
        }
    }

    @Override
    public void updatePresenterRole(int personId, String presenterRole) {
        String sql = "UPDATE presenter SET presenter_role = ? WHERE person_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, presenterRole);
            ps.setInt(2, personId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error updating presenter role", e);
        }
    }

    private Presenter mapRowToPresenter(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password");
        String fullName = rs.getString("full_name");
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        LocalDate dob = dobDate != null ? dobDate.toLocalDate() : null;
        String contact = rs.getString("contact_information");
        String presenterRole = rs.getString("presenter_role");
        String statistics = rs.getString("statistics");
        
        return new Presenter(id, username, passwordHash, fullName, dob, contact, presenterRole, statistics);
    }
}
