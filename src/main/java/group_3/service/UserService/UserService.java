package group_3.service.UserService;

import java.util.List;
import java.util.Optional;

import group_3.model.Person;
import group_3.model.enums.Role;

/**
 * @author Group 3
 *
 * UserService provides CRUD operations for all Person types.
 * This is primarily used by System Admin for user management.
 */
public interface UserService {
    Person createPerson( Person person );

    Optional<Person> getUserById(int userId);

    Optional<Person> getUserByUsername(String username);

    List<Person> getAllUsers();

    void updateUser(Person person);

    void deleteUser(int userId);

    boolean userExists(String username);

    // ======================= System Admin Functions =======================

    /**
     * Assign a new role to a user.
     * @param userId the user to update
     * @param newRole the new role to assign
     * @return true if role was successfully changed
     */
    boolean assignRole(int userId, Role newRole);

    /**
     * Get all users with a specific role.
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<Person> getUsersByRole(Role role);
}
