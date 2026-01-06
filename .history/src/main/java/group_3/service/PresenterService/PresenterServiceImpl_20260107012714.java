package group_3.service.PresenterService;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import group_3.dao.PresenterDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.PresenterDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.dao.impl.TicketDAOImpl;
import group_3.model.Presenter;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.TicketStatus;

/**
 * Implementation of PresenterService for managing Presenters and calculating statistics.
 * Provides CRUD operations and statistics calculation based on sessions and attendance.
 * 
 * @author Group21
 */
public class PresenterServiceImpl implements PresenterService {
    
    private PresenterDAO presenterDAO;
    private SessionDAO sessionDAO;
    private TicketDAO ticketDAO;
    
    public PresenterServiceImpl() {
        this.presenterDAO = new PresenterDAOImpl();
        this.sessionDAO = new SessionDAOImpl();
        this.ticketDAO = new TicketDAOImpl();
    }
    
    public PresenterServiceImpl(PresenterDAO presenterDAO, SessionDAO sessionDAO, TicketDAO ticketDAO) {
        this.presenterDAO = presenterDAO;
        this.sessionDAO = sessionDAO;
        this.ticketDAO = ticketDAO;
    }
    
    @Override
    public Presenter createPresenter(Presenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        int id = presenterDAO.create(presenter);
        return presenterDAO.findById(id).orElse(presenter);
    }
    
    @Override
    public Optional<Presenter> getPresenterById(int presenterId) {
        return presenterDAO.findById(presenterId);
    }
    
    @Override
    public Optional<Presenter> getPresenterByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return presenterDAO.findByUsername(username);
    }
    
    @Override
    public List<Presenter> getAllPresenters() {
        return presenterDAO.findAll();
    }
    
    @Override
    public void updatePersonalInfo(int presenterId, String fullName, String contactInformation) {
        Optional<Presenter> presenter = getPresenterById(presenterId);
        if (!presenter.isPresent()) {
            throw new IllegalArgumentException("Presenter with ID " + presenterId + " not found");
        }
        
        Presenter p = presenter.get();
        if (fullName != null && !fullName.trim().isEmpty()) {
            p.setFullName(fullName);
        }
        if (contactInformation != null) {
            p.setContactInformation(contactInformation);
        }
        presenterDAO.update(p);
    }
    
    @Override
    public void updatePresenterRole(int presenterId, String presenterRole) {
        presenterDAO.updatePresenterRole(presenterId, presenterRole);
    }
    
    @Override
    public void updatePresenter(Presenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        presenterDAO.update(presenter);
    }
    
    @Override
    public void deletePresenter(int presenterId) {
        presenterDAO.delete(presenterId);
    }
    
    @Override
    public int getSessionsPresented(int presenterId) {
        List<Session> allSessions = sessionDAO.findAll();
        return (int) allSessions.stream()
            .filter(session -> session.getPresenterIds() != null && 
                    session.getPresenterIds().contains(presenterId))
            .count();
    }
    
    @Override
    public int getTotalAttendeesPresented(int presenterId) {
        List<Integer> sessions = getSessionsList(presenterId);
        int totalAttendees = 0;
        
        for (Integer sessionId : sessions) {
            ArrayList<Ticket> tickets = ticketDAO.findTicketBySessionId(sessionId);
            // Count tickets that are USED or ACTIVE (attended)
            totalAttendees += (int) tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.USED || 
                                 ticket.getStatus() == TicketStatus.ACTIVE)
                .count();
        }
        
        return totalAttendees;
    }
    
    @Override
    public double getAverageAttendance(int presenterId) {
        int sessionsPresented = getSessionsPresented(presenterId);
        if (sessionsPresented == 0) {
            return 0.0;
        }
        
        int totalAttendees = getTotalAttendeesPresented(presenterId);
        return (double) totalAttendees / sessionsPresented;
    }
    
    @Override
    public Map<String, Object> getPresenterStatistics(int presenterId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        
        int sessionsPresented = getSessionsPresented(presenterId);
        int totalAttendees = getTotalAttendeesPresented(presenterId);
        double averageAttendance = getAverageAttendance(presenterId);
        
        stats.put("presenter_id", presenterId);
        stats.put("sessions_presented", sessionsPresented);
        stats.put("total_attendees", totalAttendees);
        stats.put("average_attendance", Math.round(averageAttendance * 100.0) / 100.0); // Round to 2 decimals
        stats.put("last_updated", new Date().toString());
        
        return stats;
    }
    
    @Override
    public List<Integer> getSessionsList(int presenterId) {
        List<Session> allSessions = sessionDAO.findAll();
        return allSessions.stream()
            .filter(session -> session.getPresenterIds() != null && 
                    session.getPresenterIds().contains(presenterId))
            .map(Session::getSessionId)
            .collect(Collectors.toList());
    }
    
    @Override
    public void calculateAndUpdateStatistics(int presenterId) {
        Optional<Presenter> presenter = getPresenterById(presenterId);
        if (!presenter.isPresent()) {
            throw new IllegalArgumentException("Presenter with ID " + presenterId + " not found");
        }
        
        Map<String, Object> stats = getPresenterStatistics(presenterId);
        
        // Convert map to JSON string manually
        StringBuilder jsonBuilder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            if (!first) jsonBuilder.append(",");
            jsonBuilder.append("\"").append(entry.getKey()).append("\":\"")
                .append(entry.getValue()).append("\"");
            first = false;
        }
        jsonBuilder.append("}");
        
        Presenter p = presenter.get();
        p.setStatistics(jsonBuilder.toString());
        presenterDAO.update(p);
    }
    
    @Override
    public List<Presenter> getTopPresentersBySessionCount(int limit) {
        List<Presenter> allPresenters = getAllPresenters();
        
        return allPresenters.stream()
            .sorted((p1, p2) -> Integer.compare(
                getSessionsPresented(p2.getId()),
                getSessionsPresented(p1.getId())
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Presenter> getTopPresentersByAttendance(int limit) {
        List<Presenter> allPresenters = getAllPresenters();
        
        return allPresenters.stream()
            .sorted((p1, p2) -> Integer.compare(
                getTotalAttendeesPresented(p2.getId()),
                getTotalAttendeesPresented(p1.getId())
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }
}
