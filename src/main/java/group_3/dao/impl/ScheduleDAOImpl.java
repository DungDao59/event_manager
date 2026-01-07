package group_3.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import group_3.dao.ScheduleDAO;
import group_3.model.ScheduleEntry;
import group_3.util.DatabaseConnection;

/**
 * @author Group 3
 *
 * JDBC-based DAO implementation for creating and updating
 * schedule entries in the database.
 */


public class ScheduleDAOImpl implements ScheduleDAO {
    @Override
    public void create(ScheduleEntry scheduleEntry) {
        String sql = "INSERT INTO schedule_entry (person_id, session_id, start_time, end_time) VALUES(?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, scheduleEntry.getPersonID());
            ps.setInt(2, scheduleEntry.getSessionID());
            ps.setTimestamp(3, Timestamp.valueOf(scheduleEntry.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(scheduleEntry.getEndTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ScheduleEntry scheduleEntry) {
        String sql = "UPDATE schedule_entry " +
                "SET person_id = ?, session_id = ?, start_time = ?, end_time = ? " +
                "WHERE session_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, scheduleEntry.getPersonID());
            ps.setInt(2, scheduleEntry.getSessionID());
            ps.setTimestamp(3, Timestamp.valueOf(scheduleEntry.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(scheduleEntry.getEndTime()));
            ps.setInt(5, scheduleEntry.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM schedule_entry" +
                "WHERE session_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } return false;
    }

    @Override
    public boolean deleteByUserAndSession(int userID, int sessionID) {
        String sql = "DELETE FROM schedule_entry " +
                "WHERE person_id = ? AND session_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, sessionID);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ScheduleEntry> findAllSchedule() {
        String sql = "SELECT * FROM schedule_entry";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            ArrayList<ScheduleEntry> schedule = new ArrayList<>();
            while (rs.next()) {
                schedule.add(mapRowToScheduleEntry(rs));
            } return schedule;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ScheduleEntry findById(int id) {
        String sql = "SELECT * FROM schedule_entry WHERE schedule_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return mapRowToScheduleEntry(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<ScheduleEntry> findAllScheduleByUserId(int userId) {
        String sql = "SELECT * FROM schedule_entry WHERE person_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            ArrayList<ScheduleEntry> schedule = new ArrayList<>();
            while (rs.next()) {
                schedule.add(mapRowToScheduleEntry(rs));
            } return schedule;
        } catch (SQLException e) {
            e.printStackTrace();
        } return null;
    }

    public ScheduleEntry mapRowToScheduleEntry (ResultSet rs) throws SQLException {
        int id = rs.getInt("schedule_id");
        int personID = rs.getInt("person_id");
        int sessionID = rs.getInt("session_id");
        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
        LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();

        return new ScheduleEntry(id, personID, sessionID, startTime, endTime);
    }
}
