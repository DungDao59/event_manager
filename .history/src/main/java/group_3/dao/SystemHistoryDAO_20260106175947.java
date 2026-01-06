package group_3.dao;

import java.time.LocalDate;
import java.util.List;

import group_3.model.SystemHistory;

public interface SystemHistoryDAO {
    void create(SystemHistory history);

    List<SystemHistory> findAll();

    /**
     * Find history entries by user ID (actor).
     * @param userId the user who performed the action
     * @return list of history entries for the user
     */
    List<SystemHistory> findByUserId(int userId);

    /**
     * Find history entries within a date range.
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of history entries within the date range
     */
    List<SystemHistory> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find history entries by operation type.
     * @param operationType the type of operation (e.g., "LOGIN", "LOGOUT", "BAN_USER")
     * @return list of history entries with the given operation type
     */
    List<SystemHistory> findByOperationType(String operationType);
}
