package group_3.controller;

import java.io.IOException;
import java.time.LocalDate;

import group_3.dao.impl.AttendeeDAOImpl;
import group_3.dao.impl.PersonDAOImpl;
import group_3.dao.impl.PresenterDAOImpl;
import group_3.model.Attendee;
import group_3.model.Person;
import group_3.model.Presenter;
import group_3.model.enums.Role;
import group_3.service.UserService.UserService;
import group_3.service.UserService.UserServiceImpl;
import group_3.util.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the Registration screen.
 * Handles new user registration for Attendees and Presenters.
 * 
 * @author Group 3
 */
public class RegistrationController {

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private VBox presenterRoleBox;
    @FXML private TextField presenterRoleField;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;
    @FXML private Hyperlink loginLink;

    private final UserService userService;

    public RegistrationController() {
        this.userService = new UserServiceImpl(new PersonDAOImpl());
    }

    @FXML
    public void initialize() {
        // Setup role combo box - only allow Attendee and Presenter registration
        roleComboBox.setItems(FXCollections.observableArrayList("Attendee", "Presenter"));
        roleComboBox.setValue("Attendee");

        // Show/hide presenter role field based on selection
        roleComboBox.setOnAction(e -> {
            boolean isPresenter = "Presenter".equals(roleComboBox.getValue());
            presenterRoleBox.setVisible(isPresenter);
            presenterRoleBox.setManaged(isPresenter);
        });

        hideMessage();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Gather form data
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        LocalDate dateOfBirth = dateOfBirthPicker.getValue();
        String contact = contactField.getText().trim();
        String roleStr = roleComboBox.getValue();
        String presenterRole = presenterRoleField.getText().trim();

        // Validation
        if (username.isEmpty()) {
            showError("Username is required");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        if (fullName.isEmpty()) {
            showError("Full name is required");
            return;
        }

        if (password.isEmpty()) {
            showError("Password is required");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (roleStr == null || roleStr.isEmpty()) {
            showError("Please select a role");
            return;
        }

        // Check if username exists
        if (userService.userExists(username)) {
            showError("Username already exists. Please choose another.");
            return;
        }

        // Create contact info JSON
        String contactJson = contact.isEmpty() ? "{}" : 
            String.format("{\"email\": \"%s\"}", contact);

        // Hash the password
        String passwordHash = PasswordUtil.hashPassword(password);

        try {
            Person newUser;

            if ("Presenter".equals(roleStr)) {
                newUser = new Presenter(
                    0, // ID will be generated
                    username,
                    passwordHash,
                    fullName,
                    dateOfBirth,
                    contactJson,
                    presenterRole.isEmpty() ? "Speaker" : presenterRole,
                    "{}" // Empty statistics
                );
                // Save using PresenterDAO
                new PresenterDAOImpl().create((Presenter) newUser);
            } else {
                newUser = new Attendee(
                    0, // ID will be generated
                    username,
                    passwordHash,
                    fullName,
                    dateOfBirth,
                    contactJson,
                    "{}" // Empty history
                );
                // Save using AttendeeDAO
                new AttendeeDAOImpl().create((Attendee) newUser);
            }

            showSuccess("Account created successfully! You can now sign in.");
            
            // Clear form
            clearForm();
            
            // Navigate to login after short delay
            registerButton.setDisable(true);
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::navigateToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Event Management System");
        } catch (IOException e) {
            showError("Could not load login page");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        usernameField.clear();
        fullNameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        dateOfBirthPicker.setValue(null);
        contactField.clear();
        presenterRoleField.clear();
        roleComboBox.setValue("Attendee");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60;");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void hideMessage() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }

    /**
     * Get the registration scene for programmatic navigation.
     */
    public Scene getScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Registration.fxml"));
            Parent root = loader.load();
            return new Scene(root, 550, 700);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
