package group_3.service.ScheduleService;

import java.time.*;
import group_3.model.ScheduleEntry;
import java.util.*;

public interface ScheduleService {
    boolean hasConflict(int personId, LocalDateTime startTime, LocalDateTime endTime);

    void addScheduleEntry(ScheduleEntry entry);

    void removeScheduleEntry(int personId, int sessionId);

    List<ScheduleEntry> getScheduleForPerson(int personId);
}
