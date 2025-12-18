package group_3.service.SystemHistoryService;

import group_3.model.SystemHistory;
import java.util.*;

public interface SystemHistoryService {
    void logAction(Integer user_id, String operationType, String details);

    List<SystemHistory> getAllHistory();
}
