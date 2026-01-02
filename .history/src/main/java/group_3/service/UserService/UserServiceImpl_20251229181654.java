package group_3.service.UserService;

import group_3.model.Person;
import group_3.model.Attendee;
import group_3.model.Presenter;
import group_3.dao.PersonDAO;
import group_3.dao.impl.PersonDAOImpl;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService for managing all Person types.
 * Provides CRUD operations for Attendees, Presenters, and generic Person objects.
 * 
 * @author Group21
 */
public class UserServiceImpl implements UserService {
    private final PersonDAO personDAO;

    public UserServiceImpl(PersonDAO personDao){
        this.personDAO = personDao;
    }

    @Override
    public Person createPerson(Person person){
        if(person == null){
            throw new IllegalArgumentException("Person must not be null");
        }

        if(person.getUsername() == null || person.getUsername().isBlank()){
            throw new IllegalArgumentException("Username must not be empty");
        }

        if(personDAO.findByUsername(person.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists: " + person.getUsername());
        }

        personDAO.create(person);
        return person;
    }

    @Override
    public Optional<Person> getUserById(int userId){
        if(userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return personDAO.findById(userId);
    }

    @Override
    public Optional<Person>getUserByUsername(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username must not be empty");
        }

        return personDAO.findByUsername(username);
    }

    @Override
    public List<Person> getAllUsers(){
        return personDAO.findAll();
    }

    @Override
    public void updateUser(Person person){
        if (person == null || person.getId() <= 0){
            throw new IllegalArgumentException("Invalid person data");
        }
        Optional<Person> existing = personDAO.findById(person.getId());

        if(existing.isEmpty()){
            throw new IllegalArgumentException("User not found with ID: " + person.getId());
        }

        personDAO.update(person);
    }

    @Override
    public void deleteUser(int userId){
        if(userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<Person> existing = personDAO.findById(userId);

        if(existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "User not found with ID " + userId
            );
        }

        personDAO.delete(userId);
    }

    @Override
    public boolean userExists(String username){
        if(username == null || username.isBlank()){
            return false;
        }

        return personDAO.findByUsername(username).isPresent();
    }

}
