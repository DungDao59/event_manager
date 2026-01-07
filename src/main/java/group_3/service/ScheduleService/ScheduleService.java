package group_3.service.ScheduleService;

import java.time.*;
import group_3.model.ScheduleEntry;
import java.util.*;

/**
 * @author Group 3
 *
 * Service interface defining operations for managing schedules,
 * including conflict detection and schedule entry management.
 */

public interface ScheduleService {
    boolean hasConflict(int personId, LocalDateTime startTime, LocalDateTime endTime);

    void addScheduleEntry(ScheduleEntry entry);

    void removeScheduleEntry(int personId, int sessionId);

    List<ScheduleEntry> getScheduleForPerson(int personId);
}
