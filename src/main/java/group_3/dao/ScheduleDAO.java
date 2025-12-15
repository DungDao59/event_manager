package group_3.dao;

import group_3.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ScheduleDAO {
    void create (Schedule_entry scheduleEntry);
    void update(Schedule_entry scheduleEntry);
    boolean delete(int id);
    boolean deleteByUserAndSession (int userID, int sessionID);
    Schedule_entry findById(int id);
    ArrayList<Schedule_entry> findAllSchedule();
    ArrayList<Schedule_entry> findAllScheduleByUserId(int userId);
}
