package group_3.service.SystemHistoryService;

import group_3.model.SystemHistory;
import java.util.*;

public interface SystemHistoryService {
    void logAction(int adminId, String action, String entity, int entityId, String description);

    List<SystemHistory> getAllHistory();
}
