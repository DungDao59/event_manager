package group_3.service.AttendeeService;

import group_3.model.Attendee;
import group_3.dao.AttendeeDAO;
import group_3.dao.impl.AttendeeDAOImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AttendeeService for managing Attendees.
 * Provides CRUD operations and personal information update capabilities.
 * 
 * @author Group21
 */
public class AttendeeServiceImpl implements AttendeeService {
    
    private AttendeeDAO attendeeDAO;
    
    public AttendeeServiceImpl() {
        this.attendeeDAO = new AttendeeDAOImpl();
    }
    
    public AttendeeServiceImpl(AttendeeDAO attendeeDAO) {
        this.attendeeDAO = attendeeDAO;
    }
    
    @Override
    public Attendee createAttendee(Attendee attendee) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee cannot be null");
        }
        int id = attendeeDAO.create(attendee);
        return attendeeDAO.findById(id).orElse(attendee);
    }
    
    @Override
    public Optional<Attendee> getAttendeeById(int attendeeId) {
        return attendeeDAO.findById(attendeeId);
    }
    
    @Override
    public Optional<Attendee> getAttendeeByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return attendeeDAO.findByUsername(username);
    }
    
    @Override
    public List<Attendee> getAllAttendees() {
        return attendeeDAO.findAll();
    }
    
    @Override
    public void updateFullName(int attendeeId, String fullName) {
        Optional<Attendee> attendee = getAttendeeById(attendeeId);
        if (!attendee.isPresent()) {
            throw new IllegalArgumentException("Attendee with ID " + attendeeId + " not found");
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        
        Attendee a = attendee.get();
        a.setFullName(fullName);
        attendeeDAO.update(a);
    }
    
    @Override
    public void updateDateOfBirth(int attendeeId, LocalDate dateOfBirth) {
        Optional<Attendee> attendee = getAttendeeById(attendeeId);
        if (!attendee.isPresent()) {
            throw new IllegalArgumentException("Attendee with ID " + attendeeId + " not found");
        }
        
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        
        Attendee a = attendee.get();
        a.setDateOfBirth(dateOfBirth);
        attendeeDAO.update(a);
    }
    
    @Override
    public void updateContactInformation(int attendeeId, String contactInformation) {
        Optional<Attendee> attendee = getAttendeeById(attendeeId);
        if (!attendee.isPresent()) {
            throw new IllegalArgumentException("Attendee with ID " + attendeeId + " not found");
        }
        
        Attendee a = attendee.get();
        a.setContactInformation(contactInformation);
        attendeeDAO.update(a);
    }
    
    @Override
    public void updatePersonalInfo(int attendeeId, String fullName, LocalDate dateOfBirth, String contactInformation) {
        Optional<Attendee> attendee = getAttendeeById(attendeeId);
        if (!attendee.isPresent()) {
            throw new IllegalArgumentException("Attendee with ID " + attendeeId + " not found");
        }
        
        Attendee a = attendee.get();
        
        if (fullName != null && !fullName.trim().isEmpty()) {
            a.setFullName(fullName);
        }
        
        if (dateOfBirth != null) {
            a.setDateOfBirth(dateOfBirth);
        }
        
        if (contactInformation != null) {
            a.setContactInformation(contactInformation);
        }
        
        attendeeDAO.update(a);
    }
    
    @Override
    public void updateHistory(int attendeeId, String history) {
        attendeeDAO.updateHistory(attendeeId, history);
    }
    
    @Override
    public void updateAttendee(Attendee attendee) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee cannot be null");
        }
        attendeeDAO.update(attendee);
    }
    
    @Override
    public void deleteAttendee(int attendeeId) {
        attendeeDAO.delete(attendeeId);
    }
    
    @Override
    public boolean attendeeExists(int attendeeId) {
        return getAttendeeById(attendeeId).isPresent();
    }
    
    @Override
    public int getTotalAttendeeCount() {
        return attendeeDAO.findAll().size();
    }
}
