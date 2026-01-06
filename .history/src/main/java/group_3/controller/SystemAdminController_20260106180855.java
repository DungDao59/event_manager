package group_3.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Person;
import group_3.model.SystemHistory;
import group_3.model.enums.Role;
import group_3.security.AuthContext;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.service.SystemHistoryService.SystemHistoryServiceImpl;
import group_3.service.UserService.UserService;
import group_3.service.UserService.UserServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Controller for System Admin Dashboard.
 * Provides views for System History logs and User Management.
 * 
 * @author Group 3
 */
public class SystemAdminController {

    private final SystemHistoryService historyService;
    private final UserService userService;
    private Scene scene;

    // History Tab Components
    private TableView<SystemHistory> historyTable;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> actorComboBox;
    private ComboBox<String> operationTypeComboBox;

    // User Management Tab Components
    private TableView<Person> userTable;
    private ObservableList<Person> userList;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SystemAdminController() {
        this.historyService = new SystemHistoryServiceImpl();
        this.userService = new UserServiceImpl(new PersonDAOImpl());
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

        // Header
        root.setTop(createHeader());

        // Tab Pane for different admin functions
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab historyTab = new Tab("System History", createHistoryView());
        Tab usersTab = new Tab("User Management", createUserManagementView());

        tabPane.getTabs().addAll(historyTab, usersTab);
        root.setCenter(tabPane);

        scene = new Scene(root);
        
        // Load initial data
        loadHistoryData();
        loadUserData();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("System Admin Dashboard");
        titleLabel.setFont(new Font("System Bold", 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Logged in as: " + 
            (AuthContext.getCurrentUser() != null ? AuthContext.getCurrentUser().getUsername() : "Admin"));
        userLabel.setStyle("-fx-text-fill: #ecf0f1;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutBtn);
        return header;
    }

    // ======================= History View =======================

    private VBox createHistoryView() {
        VBox historyView = new VBox(15);
        historyView.setPadding(new Insets(20));

        // Filter Section
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setPrefWidth(150);

        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setPrefWidth(150);

        actorComboBox = new ComboBox<>();
        actorComboBox.setPromptText("Filter by User");
        actorComboBox.setPrefWidth(150);
        loadActorOptions();

        operationTypeComboBox = new ComboBox<>();
        operationTypeComboBox.setPromptText("Operation Type");
        operationTypeComboBox.setPrefWidth(150);
        operationTypeComboBox.getItems().addAll(
            "All", "LOGIN", "LOGOUT", "USER_CREATED", "USER_UPDATED", 
            "USER_DELETED", "ROLE_CHANGED", "EVENT_CREATED", "EVENT_UPDATED", "EVENT_DELETED"
        );
        operationTypeComboBox.setValue("All");

        Button filterBtn = new Button("Apply Filter");
        filterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        filterBtn.setOnAction(e -> applyHistoryFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> clearHistoryFilter());

        filterBar.getChildren().addAll(
            new Label("From:"), startDatePicker,
            new Label("To:"), endDatePicker,
            new Label("User:"), actorComboBox,
            new Label("Type:"), operationTypeComboBox,
            filterBtn, clearBtn
        );

        // History Table
        historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        TableColumn<SystemHistory, Long> idCol = new TableColumn<>("Log ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        idCol.setPrefWidth(80);

        TableColumn<SystemHistory, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(cell -> {
            if (cell.getValue().getTimestamp() != null) {
                return new SimpleStringProperty(
                    cell.getValue().getTimestamp().format(DATE_TIME_FORMATTER));
            }
            return new SimpleStringProperty("N/A");
        });
        timestampCol.setPrefWidth(180);

        TableColumn<SystemHistory, String> userCol = new TableColumn<>("User ID");
        userCol.setCellValueFactory(cell -> {
            Integer userId = cell.getValue().getUserId();
            return new SimpleStringProperty(userId != null ? String.valueOf(userId) : "System");
        });
        userCol.setPrefWidth(80);

        TableColumn<SystemHistory, String> operationCol = new TableColumn<>("Operation");
        operationCol.setCellValueFactory(new PropertyValueFactory<>("operationType"));
        operationCol.setPrefWidth(150);

        TableColumn<SystemHistory, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        detailsCol.setPrefWidth(400);

        historyTable.getColumns().addAll(idCol, timestampCol, userCol, operationCol, detailsCol);

        historyView.getChildren().addAll(filterBar, historyTable);
        return historyView;
    }

    private void loadActorOptions() {
        actorComboBox.getItems().clear();
        actorComboBox.getItems().add("All Users");
        List<Person> users = userService.getAllUsers();
        for (Person user : users) {
            actorComboBox.getItems().add(user.getId() + " - " + user.getUsername());
        }
    }

    private void loadHistoryData() {
        List<SystemHistory> history = historyService.getAllHistory();
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    private void applyHistoryFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String actorSelection = actorComboBox.getValue();
        String operationType = operationTypeComboBox.getValue();

        Integer userId = null;
        if (actorSelection != null && !actorSelection.equals("All Users")) {
            try {
                userId = Integer.parseInt(actorSelection.split(" - ")[0]);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        String opType = "All".equals(operationType) ? null : operationType;

        List<SystemHistory> filtered = historyService.getFilteredHistory(
            userId, startDate, endDate, opType);
        historyTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void clearHistoryFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        actorComboBox.setValue(null);
        operationTypeComboBox.setValue("All");
        loadHistoryData();
    }

    // ======================= User Management View =======================

    private VBox createUserManagementView() {
        VBox userView = new VBox(15);
        userView.setPadding(new Insets(20));

        // Action Buttons
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadUserData());

        ComboBox<String> roleFilterCombo = new ComboBox<>();
        roleFilterCombo.setPromptText("Filter by Role");
        roleFilterCombo.getItems().addAll("All Roles", "ATTENDEE", "PRESENTER", "EVENT_ADMIN", "SYSTEM_ADMIN");
        roleFilterCombo.setValue("All Roles");
        roleFilterCombo.setOnAction(e -> filterUsersByRole(roleFilterCombo.getValue()));

        actionBar.getChildren().addAll(refreshBtn, new Label("Role:"), roleFilterCombo);

        // User Table
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        userList = FXCollections.observableArrayList();
        userTable.setItems(userList);

        TableColumn<Person, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Person, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);

        TableColumn<Person, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullNameCol.setPrefWidth(200);

        TableColumn<Person, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getRole() != null ? 
                cell.getValue().getRole().toString() : "N/A"));
        roleCol.setPrefWidth(120);

        TableColumn<Person, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button roleBtn = new Button("Change Role");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, roleBtn, deleteBtn);

            {
                roleBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5px 10px;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5px 10px;");

                roleBtn.setOnAction(e -> handleChangeRole(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDeleteUser(getTableRow().getItem()));

                buttons.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Person user = getTableRow().getItem();
                    // Cannot delete system admins
                    deleteBtn.setDisable(user.getRole() == Role.SYSTEM_ADMIN);
                    setGraphic(buttons);
                }
            }
        });

        userTable.getColumns().addAll(idCol, usernameCol, fullNameCol, roleCol, actionsCol);

        userView.getChildren().addAll(actionBar, userTable);
        return userView;
    }

    private void loadUserData() {
        userList.clear();
        userList.addAll(userService.getAllUsers());
    }

    private void filterUsersByRole(String roleStr) {
        if ("All Roles".equals(roleStr)) {
            loadUserData();
        } else {
            try {
                Role role = Role.valueOf(roleStr);
                userList.clear();
                userList.addAll(userService.getUsersByRole(role));
            } catch (IllegalArgumentException e) {
                loadUserData();
            }
        }
    }

    private void showBannedUsers() {
        userList.clear();
        userList.addAll(userService.getBannedUsers());
    }

    private void handleBanUser(Person user) {
        if (user == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ban User");
        dialog.setHeaderText("Ban user: " + user.getUsername());
        dialog.setContentText("Reason for ban:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (userService.banUser(user.getId(), reason)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "User " + user.getUsername() + " has been banned.");
                userTable.refresh();
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "User is already banned.");
            }
        });
    }

    private void handleUnbanUser(Person user) {
        if (user == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unban User");
        confirm.setHeaderText("Unban user: " + user.getUsername());
        confirm.setContentText("Are you sure you want to unban this user?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userService.unbanUser(user.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "User " + user.getUsername() + " has been unbanned.");
                userTable.refresh();
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "User is not banned.");
            }
        }
    }

    private void handleChangeRole(Person user) {
        if (user == null) return;

        ChoiceDialog<Role> dialog = new ChoiceDialog<>(user.getRole(), Role.values());
        dialog.setTitle("Change Role");
        dialog.setHeaderText("Change role for: " + user.getUsername());
        dialog.setContentText("Select new role:");

        Optional<Role> result = dialog.showAndWait();
        result.ifPresent(newRole -> {
            if (userService.assignRole(user.getId(), newRole)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Role changed to " + newRole + " (Note: Full role migration requires database update)");
                loadUserData();
            }
        });
    }

    private void handleLogout() {
        // Navigate back to login
        try {
            LoginController loginController = new LoginController();
            Scene loginScene = loginController.getScene();
            if (loginScene != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
                stage.setScene(loginScene);
                stage.setTitle("Login - Event Management System");
                stage.setWidth(500);
                stage.setHeight(600);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
