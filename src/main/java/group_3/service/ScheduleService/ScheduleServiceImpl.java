package group_3.service.ScheduleService;

import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.dao.impl.ScheduleDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.model.ScheduleEntry;
import group_3.model.Session;

import java.time.*;
import java.util.*;

/**
 * @author Group 3
 *
 * Implementation of ScheduleService, providing schedule management
 * and conflict detection for users.
 */


public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private final SessionDAO sessionDAO = new SessionDAOImpl();

    @Override
    public boolean hasConflict(int personId, LocalDateTime startTime, LocalDateTime endTime){
        List<ScheduleEntry> existingSchedules = scheduleDAO.findAllScheduleByUserId(personId);

        for(ScheduleEntry entry: existingSchedules){
            boolean overlap = startTime.isBefore(entry.getEndTime()) && endTime.isAfter(entry.getStartTime());

            if(overlap){
                return true;
            }
        }
        return false;
    }

    public boolean hasConflict(int personId, int sessionId){
        Optional<Session> optionalSession = sessionDAO.findById(sessionId);

        if(optionalSession.isEmpty()){
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        Session session = optionalSession.get();

        return hasConflict(
                personId,
                session.getStartTime(),
                session.getEndTime()
        );
    }

    @Override
    public void addScheduleEntry(ScheduleEntry entry){
        scheduleDAO.create(entry);
    }

    @Override
    public void removeScheduleEntry(int personId, int sessionId){
        scheduleDAO.deleteByUserAndSession(personId,sessionId);
    }

    @Override
    public List<ScheduleEntry> getScheduleForPerson(int personId){
        return scheduleDAO.findAllScheduleByUserId(personId);
    }
}
