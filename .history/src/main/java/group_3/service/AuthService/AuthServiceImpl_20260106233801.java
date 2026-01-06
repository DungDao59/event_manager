package group_3.service.AuthService;

import group_3.dao.PersonDAO;
import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Person;
import group_3.model.enums.Role;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.service.SystemHistoryService.SystemHistoryServiceImpl;
import group_3.util.NotificationUtil;
import group_3.util.PasswordUtil;
import group_3.security.AuthContext;

import java.util.Optional;

/**
 * @author Group3
 */
public class AuthServiceImpl implements AuthService {

    private final PersonDAO personDAO = new PersonDAOImpl();
    private Person currentUser = null;
    private final SystemHistoryService historyService = new SystemHistoryServiceImpl();

    @Override
    public Optional<Person> login(String username, String rawPassword) {
        Optional<Person> userOpt = personDAO.findByUsername(username);

        if (userOpt.isPresent()) {
            Person user = userOpt.get();
            if (PasswordUtil.verifyPassword(rawPassword, user.getPasswordHash())) {
                AuthContext.setCurrentUser(user);

                // Log action asynchronously to speed up login
                final Person finalUser = user;
                new Thread(() -> {
                    historyService.logAction(
                            finalUser.getId(),
                            "LOGIN",
                            "User logged in: " + finalUser.getUsername()
                    );
                }).start();

                NotificationUtil.notify(
                        user,
                        "Login Successfully",
                        "You have successfully logged in"
                );

                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public void logout() {
        Integer userId = AuthContext.getCurrentUserId();
        AuthContext.clear();

        historyService.logAction(
                userId,
                "LOGOUT",
                "User logged out"
        );
    }

    @Override
    public boolean isAuthenticated() {
        return AuthContext.getCurrentUser() != null;
    }

    @Override
    public boolean hasRole(Role role) {
        return isAuthenticated() && AuthContext.getCurrentUser().getRole() == role;
    }

    @Override
    public Person getCurrentUser() {
        return AuthContext.getCurrentUser();
    }
}
