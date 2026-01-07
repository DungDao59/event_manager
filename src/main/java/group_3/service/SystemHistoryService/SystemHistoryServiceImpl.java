package group_3.service.SystemHistoryService;

import group_3.dao.SystemHistoryDAO;
import group_3.dao.impl.SystemHistoryDaoImpl;
import group_3.model.SystemHistory;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Group 3
 *
 * Implementation of SystemHistoryService for logging actions
 * and retrieving system history records with optional filters.
 */


public class SystemHistoryServiceImpl implements SystemHistoryService{
    private final SystemHistoryDAO systemHistoryDAO;

    public SystemHistoryServiceImpl() {
        this.systemHistoryDAO = new SystemHistoryDaoImpl();
    }

    public SystemHistoryServiceImpl(SystemHistoryDAO systemHistoryDAO) {
        this.systemHistoryDAO = systemHistoryDAO;
    }

    @Override
    public void logAction(Integer user_id, String operationType, String details){
        SystemHistory history = new SystemHistory();

        history.setUserId(user_id);
        history.setOperationType(operationType);
        history.setDetails(details);
        history.setTimestamp(OffsetDateTime.now());

        systemHistoryDAO.create(history);
    }

    @Override
    public List<SystemHistory> getAllHistory(){
        return systemHistoryDAO.findAll();
    }

    @Override
    public List<SystemHistory> getHistoryByActor(int userId) {
        return systemHistoryDAO.findByUserId(userId);
    }

    @Override
    public List<SystemHistory> getHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        return systemHistoryDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<SystemHistory> getHistoryByOperationType(String operationType) {
        if (operationType == null || operationType.isBlank()) {
            throw new IllegalArgumentException("Operation type must not be empty");
        }
        return systemHistoryDAO.findByOperationType(operationType);
    }

    @Override
    public List<SystemHistory> getFilteredHistory(Integer userId, LocalDate startDate, 
                                                   LocalDate endDate, String operationType) {
        List<SystemHistory> result = getAllHistory();

        // Apply filters using streams
        if (userId != null) {
            result = result.stream()
                    .filter(h -> userId.equals(h.getUserId()))
                    .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            result = result.stream()
                    .filter(h -> {
                        if (h.getTimestamp() == null) return false;
                        LocalDate historyDate = h.getTimestamp().toLocalDate();
                        return !historyDate.isBefore(startDate) && !historyDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        }

        if (operationType != null && !operationType.isBlank()) {
            result = result.stream()
                    .filter(h -> operationType.equalsIgnoreCase(h.getOperationType()))
                    .collect(Collectors.toList());
        }

        return result;
    }
}
