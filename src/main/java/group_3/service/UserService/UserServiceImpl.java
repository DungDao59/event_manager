package group_3.service.UserService;

import group_3.model.Person;
import group_3.model.Attendee;
import group_3.model.Presenter;
import group_3.dao.PersonDAO;
import group_3.dao.AttendeeDAO;
import group_3.dao.PresenterDAO;
import group_3.dao.impl.PersonDAOImpl;
import group_3.dao.impl.AttendeeDAOImpl;
import group_3.dao.impl.PresenterDAOImpl;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService for managing all Person types.
 * Provides CRUD operations for Attendees, Presenters, and generic Person objects.
 * 
 * @author Group21
 */
public class UserServiceImpl implements UserService {
    
    private PersonDAO personDAO;
    private AttendeeDAO attendeeDAO;
    private PresenterDAO presenterDAO;
    
    public UserServiceImpl() {
        this.personDAO = new PersonDAOImpl();
        this.attendeeDAO = new AttendeeDAOImpl();
        this.presenterDAO = new PresenterDAOImpl();
    }
    
    public UserServiceImpl(PersonDAO personDAO, AttendeeDAO attendeeDAO, PresenterDAO presenterDAO) {
        this.personDAO = personDAO;
        this.attendeeDAO = attendeeDAO;
        this.presenterDAO = presenterDAO;
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
    public Presenter createPresenter(Presenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        int id = presenterDAO.create(presenter);
        return presenterDAO.findById(id).orElse(presenter);
    }
    
    @Override
    public Person createPerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        personDAO.create(person);
        return person;
    }
    
    @Override
    public Optional<Person> getUserById(int userId) {
        return personDAO.findById(userId);
    }
    
    @Override
    public Optional<Person> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return personDAO.findByUsername(username);
    }
    
    @Override
    public List<Person> getAllUsers() {
        return personDAO.findAll();
    }
    
    @Override
    public List<Attendee> getAllAttendees() {
        return attendeeDAO.findAll();
    }
    
    @Override
    public List<Presenter> getAllPresenters() {
        return presenterDAO.findAll();
    }
    
    @Override
    public Optional<Attendee> getAttendeeById(int attendeeId) {
        return attendeeDAO.findById(attendeeId);
    }
    
    @Override
    public Optional<Presenter> getPresenterById(int presenterId) {
        return presenterDAO.findById(presenterId);
    }
    
    @Override
    public void updateUser(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        personDAO.update(person);
    }
    
    @Override
    public void updateAttendee(Attendee attendee) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee cannot be null");
        }
        attendeeDAO.update(attendee);
    }
    
    @Override
    public void updatePresenter(Presenter presenter) {
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        presenterDAO.update(presenter);
    }
    
    @Override
    public void deleteUser(int userId) {
        personDAO.delete(userId);
    }
    
    @Override
    public void deleteAttendee(int attendeeId) {
        attendeeDAO.delete(attendeeId);
    }
    
    @Override
    public void deletePresenter(int presenterId) {
        presenterDAO.delete(presenterId);
    }
    
    @Override
    public boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return personDAO.findByUsername(username).isPresent();
    }
    
    @Override
    public int getTotalUserCount() {
        return personDAO.findAll().size();
    }
    
    @Override
    public int getTotalAttendeeCount() {
        return attendeeDAO.findAll().size();
    }
    
    @Override
    public int getTotalPresenterCount() {
        return presenterDAO.findAll().size();
    }
}
