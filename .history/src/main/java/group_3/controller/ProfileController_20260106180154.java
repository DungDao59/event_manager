package group_3.controller;

import java.time.LocalDate;
import java.util.Optional;

import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Attendee;
import group_3.model.Person;
import group_3.model.Presenter;
import group_3.model.enums.Role;
import group_3.security.AuthContext;
import group_3.service.AuthService.AuthService;
import group_3.service.AuthService.AuthServiceImpl;
import group_3.service.UserService.UserService;
import group_3.service.UserService.UserServiceImpl;
import group_3.util.PasswordUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Controller for Profile View.
 * Allows Attendees and Presenters to view and update their profile information.
 * 
 * @author Group 3
 */
public class ProfileController {

    private final UserService userService;
    private final AuthService authService;
    private Scene scene;
    private Person currentUser;

    // Profile Fields
    private TextField usernameField;
    private TextField fullNameField;
    private DatePicker dateOfBirthPicker;
    private TextField contactField;
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private TextField presenterRoleField;
    private Label messageLabel;

    public ProfileController() {
        this.userService = new UserServiceImpl(new PersonDAOImpl());
        this.authService = new AuthServiceImpl();
        this.currentUser = AuthContext.getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-color: #f5f5f5;");

        // Header
        root.setTop(createHeader());

        // Main Content
        ScrollPane scrollPane = new ScrollPane(createProfileForm());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(scrollPane);

        scene = new Scene(root);
        
        // Load user data
        loadUserData();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #3498db;");

        Label titleLabel = new Label("My Profile");
        titleLabel.setFont(new Font("System Bold", 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String username = currentUser != null ? currentUser.getUsername() : "User";
        String role = currentUser != null && currentUser.getRole() != null ? 
            currentUser.getRole().toString() : "User";
        
        Label userLabel = new Label(username + " (" + role + ")");
        userLabel.setStyle("-fx-text-fill: white;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> handleLogout());

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        backBtn.setOnAction(e -> handleBackToDashboard());

        header.getChildren().addAll(titleLabel, spacer, userLabel, backBtn, logoutBtn);
        return header;
    }

    private VBox createProfileForm() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        // Profile Card
        VBox profileCard = new VBox(20);
        profileCard.setMaxWidth(600);
        profileCard.setPadding(new Insets(30));
        profileCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label sectionTitle = new Label("Profile Information");
        sectionTitle.setFont(new Font("System Bold", 18));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Message Label
        messageLabel = new Label();
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        messageLabel.setWrapText(true);

        // Form Grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);

        int row = 0;

        // Username (read-only)
        formGrid.add(createLabel("Username"), 0, row);
        usernameField = createTextField("Username");
        usernameField.setEditable(false);
        usernameField.setStyle("-fx-background-color: #ecf0f1;");
        formGrid.add(usernameField, 1, row++);

        // Full Name
        formGrid.add(createLabel("Full Name *"), 0, row);
        fullNameField = createTextField("Enter your full name");
        formGrid.add(fullNameField, 1, row++);

        // Date of Birth
        formGrid.add(createLabel("Date of Birth"), 0, row);
        dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPrefWidth(300);
        formGrid.add(dateOfBirthPicker, 1, row++);

        // Contact Information
        formGrid.add(createLabel("Email / Contact"), 0, row);
        contactField = createTextField("Enter email or phone");
        formGrid.add(contactField, 1, row++);

        // Presenter-specific field
        if (currentUser != null && currentUser.getRole() == Role.PRESENTER) {
            formGrid.add(createLabel("Presenter Role"), 0, row);
            presenterRoleField = createTextField("e.g., Keynote Speaker");
            formGrid.add(presenterRoleField, 1, row++);
        }

        // Update Profile Button
        Button updateProfileBtn = new Button("Update Profile");
        updateProfileBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-padding: 10 30;");
        updateProfileBtn.setOnAction(e -> handleUpdateProfile());

        // Password Change Section
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        Label passwordTitle = new Label("Change Password");
        passwordTitle.setFont(new Font("System Bold", 16));
        passwordTitle.setStyle("-fx-text-fill: #2c3e50;");

        GridPane passwordGrid = new GridPane();
        passwordGrid.setHgap(15);
        passwordGrid.setVgap(15);

        int pwRow = 0;

        passwordGrid.add(createLabel("Current Password"), 0, pwRow);
        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        currentPasswordField.setPrefWidth(300);
        passwordGrid.add(currentPasswordField, 1, pwRow++);

        passwordGrid.add(createLabel("New Password"), 0, pwRow);
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        newPasswordField.setPrefWidth(300);
        passwordGrid.add(newPasswordField, 1, pwRow++);

        passwordGrid.add(createLabel("Confirm Password"), 0, pwRow);
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.setPrefWidth(300);
        passwordGrid.add(confirmPasswordField, 1, pwRow++);

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; " +
                                   "-fx-font-weight: bold; -fx-padding: 10 30;");
        changePasswordBtn.setOnAction(e -> handleChangePassword());

        profileCard.getChildren().addAll(
            sectionTitle,
            messageLabel,
            formGrid,
            updateProfileBtn,
            separator,
            passwordTitle,
            passwordGrid,
            changePasswordBtn
        );

        container.getChildren().add(profileCard);
        return container;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #34495e; -fx-font-weight: bold;");
        label.setPrefWidth(150);
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle("-fx-padding: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        return field;
    }

    private void loadUserData() {
        if (currentUser == null) {
            showError("No user logged in");
            return;
        }

        // Refresh user data from database
        Optional<Person> freshUser = userService.getUserById(currentUser.getId());
        if (freshUser.isPresent()) {
            currentUser = freshUser.get();
            AuthContext.setCurrentUser(currentUser);
        }

        usernameField.setText(currentUser.getUsername());
        fullNameField.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        dateOfBirthPicker.setValue(currentUser.getDateOfBirth());
        
        // Parse contact from JSON
        String contact = currentUser.getContactInformation();
        if (contact != null && contact.contains("\"email\"")) {
            // Simple extraction - in production use proper JSON parsing
            int start = contact.indexOf("\"email\":") + 9;
            int end = contact.indexOf("\"", start + 1);
            if (end > start) {
                contactField.setText(contact.substring(start, end).replace("\"", ""));
            }
        }

        // Load presenter-specific data
        if (currentUser instanceof Presenter && presenterRoleField != null) {
            presenterRoleField.setText(((Presenter) currentUser).getPresenterRole());
        }
    }

    private void handleUpdateProfile() {
        String fullName = fullNameField.getText().trim();
        LocalDate dob = dateOfBirthPicker.getValue();
        String contact = contactField.getText().trim();

        if (fullName.isEmpty()) {
            showError("Full name is required");
            return;
        }

        try {
            // Update the user object
            currentUser.setFullName(fullName);
            currentUser.setDateOfBirth(dob);
            
            String contactJson = contact.isEmpty() ? "{}" : 
                String.format("{\"email\": \"%s\"}", contact);
            currentUser.setContactInformation(contactJson);

            // Update presenter role if applicable
            if (currentUser instanceof Presenter && presenterRoleField != null) {
                ((Presenter) currentUser).setPresenterRole(presenterRoleField.getText().trim());
            }

            // Save to database
            userService.updateUser(currentUser);
            
            showSuccess("Profile updated successfully!");
        } catch (Exception e) {
            showError("Failed to update profile: " + e.getMessage());
        }
    }

    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty()) {
            showError("Please enter your current password");
            return;
        }

        if (newPassword.isEmpty()) {
            showError("Please enter a new password");
            return;
        }

        if (newPassword.length() < 6) {
            showError("New password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match");
            return;
        }

        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
            showError("Current password is incorrect");
            return;
        }

        try {
            // Hash and set new password
            String newHash = PasswordUtil.hashPassword(newPassword);
            currentUser.setPasswordHash(newHash);
            
            // Save to database
            userService.updateUser(currentUser);
            
            // Clear password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            
            showSuccess("Password changed successfully!");
        } catch (Exception e) {
            showError("Failed to change password: " + e.getMessage());
        }
    }

    private void handleLogout() {
        authService.logout();
        navigateToLogin();
    }

    private void handleBackToDashboard() {
        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        Stage stage = (Stage) scene.getWindow();
        Role role = currentUser.getRole();

        if (role == Role.SYSTEM_ADMIN) {
            SystemAdminController controller = new SystemAdminController();
            stage.setScene(controller.getScene());
            stage.setTitle("System Admin Dashboard");
        } else if (role == Role.EVENT_ADMIN) {
            EventListController controller = new EventListController();
            stage.setScene(controller.getScene());
            stage.setTitle("Event Management");
        } else {
            // For attendees and presenters, profile is the main view
            showInfo("You are already on your main dashboard.");
        }
    }

    private void navigateToLogin() {
        try {
            LoginController loginController = new LoginController();
            Scene loginScene = loginController.getScene();
            if (loginScene != null) {
                Stage stage = (Stage) scene.getWindow();
                stage.setScene(loginScene);
                stage.setTitle("Login - Event Management System");
                stage.setWidth(500);
                stage.setHeight(600);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
