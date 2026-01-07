package group_3.service.AuthService;

import java.util.Optional;

import group_3.model.Person;
import group_3.model.enums.Role;

/**
 * @author Group 3
 *
 * Service interface defining authentication operations,
 * including login, logout, role checking, and current user retrieval.
 */


public interface AuthService {
    Optional<Person> login(String username, String rawPassword);

    void logout();

    boolean isAuthenticated();

    boolean hasRole( Role role );

    Person getCurrentUser();
}
