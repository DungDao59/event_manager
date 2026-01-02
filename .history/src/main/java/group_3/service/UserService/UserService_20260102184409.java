package group_3.service.UserService;

import java.util.List;
import java.util.Optional;

import group_3.model.Person;

/**
 * UserService provides CRUD operations for all Person types.
 * This is primarily used by System Admin for user management.
 * 
 * @author Group21
 */
public interface UserService {
    Person createPerson( Person person );

    Optional<Person> getUserById(int userId);

    Optional<Person> getUserByUsername(String username);

    List<Person> getAllUsers();

    void updateUser(Person person);

    void deleteUser(int userId);

    boolean userExists(String username);
}
