package group_3.service.SystemHistoryService;

import java.time.LocalDate;
import java.util.List;

import group_3.model.SystemHistory;

public interface SystemHistoryService {
    void logAction(Integer user_id, String operationType, String details);

    List<SystemHistory> getAllHistory();

    /**
     * Get history entries filtered by actor (user ID).
     * @param userId the user who performed the actions
     * @return list of history entries for the user
     */
    List<SystemHistory> getHistoryByActor(int userId);

    /**
     * Get history entries filtered by date range.
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of history entries within the date range
     */
    List<SystemHistory> getHistoryByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get history entries filtered by operation type.
     * @param operationType the type of operation
     * @return list of history entries with the given operation type
     */
    List<SystemHistory> getHistoryByOperationType(String operationType);

    /**
     * Get history entries with combined filters.
     * @param userId optional user ID filter (null to ignore)
     * @param startDate optional start date filter (null to ignore)
     * @param endDate optional end date filter (null to ignore)
     * @param operationType optional operation type filter (null to ignore)
     * @return filtered list of history entries
     */
    List<SystemHistory> getFilteredHistory(Integer userId, LocalDate startDate, 
                                           LocalDate endDate, String operationType);
}
