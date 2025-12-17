package group_3.dao.impl;

import group_3.dao.ScheduleDAO;
import group_3.model.Schedule_entry;
import group_3.model.Ticket;
import group_3.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ScheduleDAOImpl implements ScheduleDAO {
    @Override
    public void create(Schedule_entry scheduleEntry) {
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
    public void update(Schedule_entry scheduleEntry) {
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
    public ArrayList<Schedule_entry> findAllSchedule() {
        String sql = "SELECT * FROM schedule_entry";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            ArrayList<Schedule_entry> schedule = new ArrayList<>();
            while (rs.next()) {
                schedule.add(mapRowToScheduleEntry(rs));
            } return schedule;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Schedule_entry findById(int id) {
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
    public ArrayList<Schedule_entry> findAllScheduleByUserId(int userId) {
        String sql = "SELECT * FROM schedule_entry WHERE person_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Schedule_entry> schedule = new ArrayList<>();
            while (rs.next()) {
                schedule.add(mapRowToScheduleEntry(rs));
            } return schedule;
        } catch (SQLException e) {
            e.printStackTrace();
        } return null;
    }

    public Schedule_entry mapRowToScheduleEntry (ResultSet rs) throws SQLException {
        int id = rs.getInt("schedule_id");
        int personID = rs.getInt("person_id");
        int sessionID = rs.getInt("session_id");
        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
        LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();

        return new Schedule_entry(id, personID, sessionID, startTime, endTime);
    }
}
