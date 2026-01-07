package group_3.service.AttendeeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import group_3.dao.PersonDAO;
import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Attendee;

/**
 * @author Group 3
 *
 * Implementation of AttendeeService for managing Attendees.
 * Provides CRUD operations and personal information update capabilities.
 */
public class AttendeeServiceImpl implements AttendeeService {
    
    private PersonDAO personDAO;
    
    public AttendeeServiceImpl() {
        this.personDAO = new PersonDAOImpl();
    }
    
    public AttendeeServiceImpl(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }
    
    @Override
    public Attendee createAttendee(Attendee attendee) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee cannot be null");
        }
        personDAO.create(attendee);
        return attendee;
    }
    
    @Override
    public Optional<Attendee> getAttendeeById(int attendeeId) {
        Optional<group_3.model.Person> person = personDAO.findById(attendeeId);
        if (person.isPresent() && person.get() instanceof Attendee) {
            return Optional.of((Attendee) person.get());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Attendee> getAttendeeByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        Optional<group_3.model.Person> person = personDAO.findByUsername(username);
        if (person.isPresent() && person.get() instanceof Attendee) {
            return Optional.of((Attendee) person.get());
        }
        return Optional.empty();
    }
    
    @Override
    public List<Attendee> getAllAttendees() {
        return personDAO.findAll().stream()
            .filter(person -> person instanceof Attendee)
            .map(person -> (Attendee) person)
            .collect(Collectors.toList());
    }

    @Override
    public void updateHistory(int attendeeId, String history) {
        Optional<Attendee> attendee = getAttendeeById(attendeeId);
        if (!attendee.isPresent()) {
            throw new IllegalArgumentException("Attendee with ID " + attendeeId + " not found");
        }
        
        Attendee a = attendee.get();
        a.setHistory(history);
        personDAO.update(a);
    }
    
    @Override
    public void updateAttendee(Attendee attendee) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee cannot be null");
        }
        personDAO.update(attendee);
    }
    
    @Override
    public void deleteAttendee(int attendeeId) {
        personDAO.delete(attendeeId);
    }
    
    @Override
    public boolean attendeeExists(int attendeeId) {
        return getAttendeeById(attendeeId).isPresent();
    }
    
    @Override
    public int getTotalAttendeeCount() {
        return (int) personDAO.findAll().stream()
            .filter(person -> person instanceof Attendee)
            .count();
    }
}
