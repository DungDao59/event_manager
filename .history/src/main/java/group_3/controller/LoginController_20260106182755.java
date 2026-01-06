package group_3.controller;

import java.io.IOException;
import java.util.Optional;

import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Person;
import group_3.model.enums.Role;
import group_3.service.AuthService.AuthService;
import group_3.service.AuthService.AuthServiceImpl;
import group_3.service.UserService.UserService;
import group_3.service.UserService.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Login screen.
 * Handles user authentication and navigation to registration.
 * 
 * @author Group 3
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button guestButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink registerLink;

    private final AuthService authService;
    private final UserService userService;

    public LoginController() {
        this.authService = new AuthServiceImpl();
        this.userService = new UserServiceImpl(new PersonDAOImpl());
    }

    @FXML
    public void initialize() {
        // Clear any previous error messages
        hideError();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty()) {
            showError("Please enter your username");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        // Attempt login
        Optional<Person> userOpt = authService.login(username, password);

        if (userOpt.isPresent()) {
            Person user = userOpt.get();
            hideError();
            navigateToMainScreen(user);
        } else {
            showError("Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Registration.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register - Event Management System");
        } catch (IOException e) {
            showError("Could not load registration page");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseAsGuest(ActionEvent event) {
        try {
            Stage stage = (Stage) guestButton.getScene().getWindow();
            PublicEventBrowserController browserController = new PublicEventBrowserController();
            stage.setScene(browserController.getScene());
            stage.setTitle("Event Discovery - Browse Events");
            stage.setWidth(1200);
            stage.setHeight(800);
        } catch (Exception e) {
            showError("Could not load event browser");
            e.printStackTrace();
        }
    }

    private void navigateToMainScreen(Person user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Navigate based on role
            Role role = user.getRole();
            
            if (role == Role.SYSTEM_ADMIN) {
                // Load System Admin Dashboard
                loadSystemAdminDashboard(stage);
            } else if (role == Role.EVENT_ADMIN) {
                // Load Event Management (existing EventListController)
                loadEventManagement(stage);
            } else if (role == Role.PRESENTER) {
                // Load Presenter Dashboard
                loadPresenterDashboard(stage);
            } else {
                // Load Attendee Dashboard (default)
                loadAttendeeDashboard(stage);
            }
        } catch (Exception e) {
            showError("Could not load main screen");
            e.printStackTrace();
        }
    }

    private void loadEventManagement(Stage stage) {
        EventListController controller = new EventListController();
        stage.setScene(controller.getScene());
        stage.setTitle("Event Management System");
        stage.setWidth(1200);
        stage.setHeight(800);
    }

    private void loadSystemAdminDashboard(Stage stage) {
        SystemAdminController controller = new SystemAdminController();
        stage.setScene(controller.getScene());
        stage.setTitle("System Admin Dashboard");
        stage.setWidth(1200);
        stage.setHeight(800);
    }

    private void loadPresenterDashboard(Stage stage) {
        // For now, load profile view - can be extended
        ProfileController controller = new ProfileController();
        stage.setScene(controller.getScene());
        stage.setTitle("Presenter Dashboard");
        stage.setWidth(1000);
        stage.setHeight(700);
    }

    private void loadAttendeeDashboard(Stage stage) {
        // For now, load profile view - can be extended
        ProfileController controller = new ProfileController();
        stage.setScene(controller.getScene());
        stage.setTitle("Attendee Dashboard");
        stage.setWidth(1000);
        stage.setHeight(700);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Get the login scene for programmatic navigation.
     */
    public Scene getScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            return new Scene(root, 500, 600);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
