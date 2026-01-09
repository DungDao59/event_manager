package group_3.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import group_3.dao.ScheduleDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Event;
import group_3.model.EventStatistics;
import group_3.model.Person;
import group_3.model.ScheduleEntry;
import group_3.model.Session;
import group_3.model.SystemHistory;
import group_3.model.Ticket;
import group_3.model.enums.Role;
import group_3.model.enums.TicketStatus;
import group_3.service.EventAdminService.EventAdminService;
import group_3.service.EventAdminService.EventAdminServiceImpl;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.service.SystemHistoryService.SystemHistoryServiceImpl;
import group_3.service.UserService.UserService;
import group_3.service.UserService.UserServiceImpl;
import group_3.util.BulkDataLoader;
import group_3.util.DaoProvider;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * @author Group 3
 *
 * Controller for System Admin Dashboard.
 * Provides views for System History logs and User Management.
 */
public class SystemAdminController {

    // Services
    private final SystemHistoryService historyService;
    private final UserService userService;
    private final EventAdminService eventAdminService;
    private final TicketDAO ticketDAO;
    private final ScheduleDAO scheduleDAO;

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

    // Event Management Tab Components
    private TableView<Event> eventTable;
    private ObservableList<Event> eventList;

    // Session Management Tab Components
    private TableView<Session> sessionTable;
    private ObservableList<Session> sessionList;

    // Ticket Management Tab Components
    private TableView<Ticket> ticketTable;
    private ObservableList<Ticket> ticketList;

    // Schedule Management Tab Components
    private TableView<ScheduleEntry> scheduleTable;
    private ObservableList<ScheduleEntry> scheduleList;

    // Statistics Tab Components
    private TableView<EventStatistics> statsTable;
    private Label totalEventsValue;
    private Label totalUsersValue;
    private Label totalTicketsValue;
    private Label totalRevenueValue;

    // Loading overlay components
    private StackPane loadingOverlay;
    private Label loadingLabel;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SystemAdminController() {
        this.historyService = new SystemHistoryServiceImpl();
        this.userService = new UserServiceImpl(new PersonDAOImpl());
        this.eventAdminService = new EventAdminServiceImpl();
        
        this.ticketDAO = DaoProvider.getTicketDAO();
        this.scheduleDAO = DaoProvider.getScheduleDAO();
        
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        root.setTop(createHeader());

        // Create TabPane with ALL admin functions
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab historyTab = new Tab("System History", createHistoryView());
        Tab usersTab = new Tab("User Management", createUserManagementView());
        Tab eventsTab = new Tab("Event Management", createEventManagementView());
        Tab sessionsTab = new Tab("Session Management", createSessionManagementView());
        Tab ticketsTab = new Tab("Ticket Management", createTicketManagementView());
        Tab schedulesTab = new Tab("Schedule Management", createScheduleManagementView());
        Tab statsTab = new Tab("Reports & Statistics", createStatisticsView());

        tabPane.getTabs().addAll(historyTab, usersTab, eventsTab, sessionsTab, ticketsTab, schedulesTab, statsTab);

        // Create loading overlay
        loadingOverlay = new StackPane();
        loadingOverlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(80, 80);
        loadingLabel = new Label("Loading data...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox loadingContent = new VBox(15, progressIndicator, loadingLabel);
        loadingContent.setAlignment(Pos.CENTER);
        loadingOverlay.getChildren().add(loadingContent);
        loadingOverlay.setVisible(true);

        // Wrap tabPane with loading overlay
        StackPane contentWrapper = new StackPane(tabPane, loadingOverlay);
        root.setCenter(contentWrapper);

        scene = new Scene(root);
        
        // Load all data asynchronously using BulkDataLoader (single connection)
        loadAllDataAsync();
    }

    private void loadAllDataAsync() {
        // Show loading overlay
        Platform.runLater(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisible(true);
                loadingLabel.setText("Loading data...");
            }
        });

        Thread loadThread = new Thread(() -> {
            try {
               
                // Use BulkDataLoader - ONE connection for ALL data including statistics
                BulkDataLoader.SystemAdminData data = BulkDataLoader.loadSystemAdminData();
                   
                final BulkDataLoader.SystemAdminData finalData = data;
                
                Platform.runLater(() -> {
                    // Update all tables
                    historyTable.setItems(FXCollections.observableArrayList(finalData.history));
                    userList.clear();
                    userList.addAll(finalData.users);
                    eventList.clear();
                    eventList.addAll(finalData.events);
                    sessionList.clear();
                    sessionList.addAll(finalData.sessions);
                    ticketList.clear();
                    ticketList.addAll(finalData.tickets);
                    scheduleList.clear();
                    scheduleList.addAll(finalData.schedules);
                    statsTable.setItems(FXCollections.observableArrayList(finalData.statistics));
                    
                    // Update statistics cards
                    if (totalEventsValue != null) {
                        totalEventsValue.setText(String.valueOf(finalData.events.size()));
                        totalUsersValue.setText(String.valueOf(finalData.users.size()));
                        totalTicketsValue.setText(String.valueOf(finalData.tickets.size()));
                        double revenue = finalData.tickets.stream().mapToDouble(Ticket::getPrice).sum();
                        totalRevenueValue.setText(String.format("$%.2f", revenue));
                    }
                    
                    // Load actor options for history filter
                    actorComboBox.getItems().clear();
                    actorComboBox.getItems().add("All Users");
                    for (Person user : finalData.users) {
                        actorComboBox.getItems().add(user.getId() + " - " + user.getUsername());
                    }
                    
                    // Hide loading overlay
                    if (loadingOverlay != null) {
                        loadingOverlay.setVisible(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (loadingOverlay != null) {
                        loadingOverlay.setVisible(false);
                    }
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // ======================= HEADER =======================

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("System Admin Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> handleLogout());

        header.getChildren().addAll(titleLabel, spacer, logoutBtn);
        return header;
    }

    // ======================= TAB 1: SYSTEM HISTORY =======================

    private VBox createHistoryView() {
        VBox historyView = new VBox(15);
        historyView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Complete System Operation History");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setPrefWidth(130);

        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setPrefWidth(130);

        actorComboBox = new ComboBox<>();
        actorComboBox.setPromptText("Filter by User");
        actorComboBox.setPrefWidth(150);

        operationTypeComboBox = new ComboBox<>();
        operationTypeComboBox.setPromptText("Type");
        operationTypeComboBox.setPrefWidth(150);
        operationTypeComboBox.getItems().addAll("All", "LOGIN", "LOGOUT", "USER_CREATED", "USER_UPDATED", 
            "USER_DELETED", "ROLE_CHANGED", "EVENT_CREATED", "EVENT_UPDATED", "EVENT_DELETED", "DATA_SEED");
        operationTypeComboBox.setValue("All");

        Button filterBtn = new Button("Apply Filter");
        filterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        filterBtn.setOnAction(e -> applyHistoryFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> clearHistoryFilter());

        filterBar.getChildren().addAll(new Label("From:"), startDatePicker, new Label("To:"), endDatePicker,
            new Label("User:"), actorComboBox, new Label("Type:"), operationTypeComboBox, filterBtn, clearBtn);

        historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        TableColumn<SystemHistory, Long> idCol = new TableColumn<>("Log ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        idCol.setPrefWidth(70);

        TableColumn<SystemHistory, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(cell -> {
            if (cell.getValue().getTimestamp() != null) {
                return new SimpleStringProperty(cell.getValue().getTimestamp().format(DATE_TIME_FORMATTER));
            }
            return new SimpleStringProperty("N/A");
        });
        timestampCol.setPrefWidth(160);

        TableColumn<SystemHistory, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(70);

        TableColumn<SystemHistory, String> operationCol = new TableColumn<>("Operation");
        operationCol.setCellValueFactory(new PropertyValueFactory<>("operationType"));
        operationCol.setPrefWidth(120);

        TableColumn<SystemHistory, String> detailsCol = new TableColumn<>("Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        detailsCol.setPrefWidth(400);

        historyTable.getColumns().addAll(idCol, timestampCol, userIdCol, operationCol, detailsCol);

        historyView.getChildren().addAll(sectionTitle, filterBar, historyTable);
        return historyView;
    }

    private void loadHistoryData() {
        try {
            historyTable.setItems(FXCollections.observableArrayList(historyService.getAllHistory()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } catch (NumberFormatException e) { }
        }

        String opType = "All".equals(operationType) ? null : operationType;
        try {
            List<SystemHistory> filtered = historyService.getFilteredHistory(userId, startDate, endDate, opType);
            historyTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter: " + e.getMessage());
        }
    }

    private void clearHistoryFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        actorComboBox.setValue(null);
        operationTypeComboBox.setValue("All");
        loadHistoryData();
    }

    // ======================= TAB 2: USER MANAGEMENT =======================

    private VBox createUserManagementView() {
        VBox userView = new VBox(15);
        userView.setPadding(new Insets(20));

        Label sectionTitle = new Label("User Management - CRUD Operations");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadUserData());

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.setPromptText("Filter by Role");
        roleFilter.getItems().addAll("All Roles", "ATTENDEE", "PRESENTER", "EVENT_ADMIN", "SYSTEM_ADMIN");
        roleFilter.setValue("All Roles");
        roleFilter.setOnAction(e -> filterUsersByRole(roleFilter.getValue()));

        actionBar.getChildren().addAll(refreshBtn, new Label("Role:"), roleFilter);

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
        roleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole() != null ? cell.getValue().getRole().toString() : "N/A"));
        roleCol.setPrefWidth(120);

        TableColumn<Person, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button roleBtn = new Button("Change Role");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, roleBtn, deleteBtn);

            {
                roleBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5px 8px;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5px 8px;");
                roleBtn.setOnAction(e -> { Person user = getTableRow().getItem(); if (user != null) handleChangeRole(user); });
                deleteBtn.setOnAction(e -> { Person user = getTableRow().getItem(); if (user != null) handleDeleteUser(user); });
                buttons.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Person user = getTableRow().getItem();
                    deleteBtn.setDisable(user.getRole() == Role.SYSTEM_ADMIN);
                    setGraphic(buttons);
                }
            }
        });

        userTable.getColumns().addAll(idCol, usernameCol, fullNameCol, roleCol, actionsCol);
        userView.getChildren().addAll(sectionTitle, actionBar, userTable);
        return userView;
    }

    private void loadUserData() {
        try { userList.clear(); userList.addAll(userService.getAllUsers()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void filterUsersByRole(String roleStr) {
        if ("All Roles".equals(roleStr)) { loadUserData(); }
        else {
            try { Role role = Role.valueOf(roleStr); userList.clear(); userList.addAll(userService.getUsersByRole(role)); } 
            catch (Exception e) { loadUserData(); }
        }
    }

    private void handleChangeRole(Person user) {
        ChoiceDialog<Role> dialog = new ChoiceDialog<>(user.getRole(), Role.values());
        dialog.setTitle("Change Role");
        dialog.setHeaderText("Change role for: " + user.getUsername());
        dialog.setContentText("Select new role:");
        Optional<Role> result = dialog.showAndWait();
        result.ifPresent(newRole -> {
            if (userService.assignRole(user.getId(), newRole)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Role changed to " + newRole);
                loadUserData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to change role.");
            }
        });
    }

    private void handleDeleteUser(Person user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete user: " + user.getUsername());
        confirm.setContentText("Are you sure?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { userService.deleteUser(user.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted.");
                loadUserData();
            }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    // ======================= TAB 3: EVENT MANAGEMENT =======================

    private VBox createEventManagementView() {
        VBox eventView = new VBox(15);
        eventView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Event Management - CRUD Operations");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button createBtn = new Button("+ Create Event");
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        createBtn.setOnAction(e -> handleCreateEvent());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadEventData());

        actionBar.getChildren().addAll(createBtn, refreshBtn);

        eventTable = new TableView<>();
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(eventTable, Priority.ALWAYS);
        eventList = FXCollections.observableArrayList();
        eventTable.setItems(eventList);

        TableColumn<Event, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        idCol.setPrefWidth(60);

        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Event, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType() != null ? cell.getValue().getType().toString() : "N/A"));
        typeCol.setPrefWidth(120);

        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);

        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus() != null ? cell.getValue().getStatus().toString() : "N/A"));
        statusCol.setPrefWidth(100);

        TableColumn<Event, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5px 8px;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5px 8px;");
                editBtn.setOnAction(e -> { Event event = getTableRow().getItem(); if (event != null) handleEditEvent(event); });
                deleteBtn.setOnAction(e -> { Event event = getTableRow().getItem(); if (event != null) handleDeleteEvent(event); });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : buttons);
            }
        });

        eventTable.getColumns().addAll(idCol, nameCol, typeCol, locationCol, statusCol, actionsCol);
        eventView.getChildren().addAll(sectionTitle, actionBar, eventTable);
        return eventView;
    }

    private void loadEventData() {
        try { eventList.clear(); eventList.addAll(eventAdminService.getAllEvents()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleCreateEvent() {
        try { EventFormController form = new EventFormController(null, null); form.show(); loadEventData(); } 
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
    }

    private void handleEditEvent(Event event) {
        try { EventFormController form = new EventFormController(event, null); form.show(); loadEventData(); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
    }

    private void handleDeleteEvent(Event event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Event");
        confirm.setHeaderText("Delete: " + event.getName());
        confirm.setContentText("This will delete all related sessions and tickets. Continue?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { eventAdminService.deleteEvent(event.getEventId()); showAlert(Alert.AlertType.INFORMATION, "Success", "Event deleted."); loadEventData(); loadSessionData(); loadTicketData(); }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    // ======================= TAB 4: SESSION MANAGEMENT =======================

    private VBox createSessionManagementView() {
        VBox sessionView = new VBox(15);
        sessionView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Session Management - CRUD Operations");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox actionBar = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadSessionData());
        actionBar.getChildren().add(refreshBtn);

        sessionTable = new TableView<>();
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(sessionTable, Priority.ALWAYS);
        sessionList = FXCollections.observableArrayList();
        sessionTable.setItems(sessionList);

        TableColumn<Session, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        idCol.setPrefWidth(60);

        TableColumn<Session, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);

        TableColumn<Session, Integer> eventIdCol = new TableColumn<>("Event ID");
        eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        eventIdCol.setPrefWidth(80);

        TableColumn<Session, String> venueCol = new TableColumn<>("Venue");
        venueCol.setCellValueFactory(new PropertyValueFactory<>("venue"));
        venueCol.setPrefWidth(150);

        TableColumn<Session, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(100);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            { deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"); 
              deleteBtn.setOnAction(e -> { Session s = getTableRow().getItem(); if (s != null) handleDeleteSession(s); }); }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty || getTableRow().getItem() == null ? null : deleteBtn); }
        });

        sessionTable.getColumns().addAll(idCol, titleCol, eventIdCol, venueCol, actionsCol);
        sessionView.getChildren().addAll(sectionTitle, actionBar, sessionTable);
        return sessionView;
    }

    private void loadSessionData() {
        try { sessionList.clear(); sessionList.addAll(eventAdminService.getAllSessions()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDeleteSession(Session session) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Session");
        confirm.setHeaderText("Delete: " + session.getTitle());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { eventAdminService.deleteSession(session.getSessionId()); showAlert(Alert.AlertType.INFORMATION, "Success", "Session deleted."); loadSessionData(); }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    // ======================= TAB 5: TICKET MANAGEMENT =======================

    private VBox createTicketManagementView() {
        VBox ticketView = new VBox(15);
        ticketView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Ticket Management - CRUD Operations");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox actionBar = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadTicketData());

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "ACTIVE", "USED", "CANCELLED", "EXPIRED");
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> filterTicketsByStatus(statusFilter.getValue()));

        actionBar.getChildren().addAll(refreshBtn, new Label("Status:"), statusFilter);

        ticketTable = new TableView<>();
        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(ticketTable, Priority.ALWAYS);
        ticketList = FXCollections.observableArrayList();
        ticketTable.setItems(ticketList);

        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("ticketID"));
        idCol.setPrefWidth(60);

        TableColumn<Ticket, Integer> attendeeCol = new TableColumn<>("Attendee ID");
        attendeeCol.setCellValueFactory(new PropertyValueFactory<>("attendeeID"));
        attendeeCol.setPrefWidth(100);

        TableColumn<Ticket, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);

        TableColumn<Ticket, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus() != null ? cell.getValue().getStatus().toString() : "N/A"));
        statusCol.setPrefWidth(100);

        TableColumn<Ticket, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, cancelBtn, deleteBtn);
            {
                cancelBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                cancelBtn.setOnAction(e -> { Ticket t = getTableRow().getItem(); if (t != null) handleCancelTicket(t); });
                deleteBtn.setOnAction(e -> { Ticket t = getTableRow().getItem(); if (t != null) handleDeleteTicket(t); });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setGraphic(null); }
                else { Ticket t = getTableRow().getItem(); cancelBtn.setDisable(t.getStatus() != TicketStatus.ACTIVE); setGraphic(buttons); }
            }
        });

        ticketTable.getColumns().addAll(idCol, attendeeCol, priceCol, statusCol, actionsCol);
        ticketView.getChildren().addAll(sectionTitle, actionBar, ticketTable);
        return ticketView;
    }

    private void loadTicketData() {
        try { ticketList.clear(); ticketList.addAll(ticketDAO.findAll()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void filterTicketsByStatus(String status) {
        if ("All".equals(status)) { loadTicketData(); }
        else { try { TicketStatus ts = TicketStatus.valueOf(status); ticketList.clear(); ticketList.addAll(ticketDAO.findAll().stream().filter(t -> t.getStatus() == ts).toList()); } catch (Exception e) { loadTicketData(); } }
    }

    private void handleCancelTicket(Ticket ticket) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Ticket");
        confirm.setHeaderText("Cancel ticket ID: " + ticket.getTicketID());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { ticket.setStatus(TicketStatus.CANCELLED); ticketDAO.update(ticket); showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket cancelled."); loadTicketData(); }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    private void handleDeleteTicket(Ticket ticket) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Ticket");
        confirm.setHeaderText("Delete ticket ID: " + ticket.getTicketID());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { ticketDAO.delete(ticket.getTicketID()); showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket deleted."); loadTicketData(); }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    // ======================= TAB 6: SCHEDULE MANAGEMENT =======================

    private VBox createScheduleManagementView() {
        VBox scheduleView = new VBox(15);
        scheduleView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Schedule Management - CRUD Operations");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox actionBar = new HBox(10);
        Button addBtn = new Button("+ Add Schedule");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> handleAddSchedule());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadScheduleData());

        actionBar.getChildren().addAll(addBtn, refreshBtn);

        scheduleTable = new TableView<>();
        scheduleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(scheduleTable, Priority.ALWAYS);
        scheduleList = FXCollections.observableArrayList();
        scheduleTable.setItems(scheduleList);

        TableColumn<ScheduleEntry, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<ScheduleEntry, Integer> personCol = new TableColumn<>("Person ID");
        personCol.setCellValueFactory(new PropertyValueFactory<>("personID"));
        personCol.setPrefWidth(100);

        TableColumn<ScheduleEntry, Integer> sessionCol = new TableColumn<>("Session ID");
        sessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionID"));
        sessionCol.setPrefWidth(100);

        TableColumn<ScheduleEntry, LocalDateTime> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        startCol.setPrefWidth(180);

        TableColumn<ScheduleEntry, LocalDateTime> endCol = new TableColumn<>("End Time");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        endCol.setPrefWidth(180);

        TableColumn<ScheduleEntry, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                editBtn.setOnAction(e -> { ScheduleEntry s = getTableRow().getItem(); if (s != null) handleEditSchedule(s); });
                deleteBtn.setOnAction(e -> { ScheduleEntry s = getTableRow().getItem(); if (s != null) handleDeleteSchedule(s); });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty || getTableRow().getItem() == null ? null : buttons); }
        });

        scheduleTable.getColumns().addAll(idCol, personCol, sessionCol, startCol, endCol, actionsCol);
        scheduleView.getChildren().addAll(sectionTitle, actionBar, scheduleTable);
        return scheduleView;
    }

    private void loadScheduleData() {
        try { scheduleList.clear(); scheduleList.addAll(scheduleDAO.findAllSchedule()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleAddSchedule() {
        Dialog<ScheduleEntry> dialog = new Dialog<>();
        dialog.setTitle("Add Schedule");
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField personIdField = new TextField(); personIdField.setPromptText("Person ID");
        TextField sessionIdField = new TextField(); sessionIdField.setPromptText("Session ID");
        TextField startTimeField = new TextField(); startTimeField.setPromptText("yyyy-MM-dd HH:mm");
        TextField endTimeField = new TextField(); endTimeField.setPromptText("yyyy-MM-dd HH:mm");

        grid.add(new Label("Person ID:"), 0, 0); grid.add(personIdField, 1, 0);
        grid.add(new Label("Session ID:"), 0, 1); grid.add(sessionIdField, 1, 1);
        grid.add(new Label("Start Time:"), 0, 2); grid.add(startTimeField, 1, 2);
        grid.add(new Label("End Time:"), 0, 3); grid.add(endTimeField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    ScheduleEntry s = new ScheduleEntry();
                    s.setPersonID(Integer.parseInt(personIdField.getText().trim()));
                    s.setSessionID(Integer.parseInt(sessionIdField.getText().trim()));
                    s.setStartTime(LocalDateTime.parse(startTimeField.getText().trim(), fmt));
                    s.setEndTime(LocalDateTime.parse(endTimeField.getText().trim(), fmt));
                    return s;
                } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid input: " + e.getMessage()); return null; }
            }
            return null;
        });

        Optional<ScheduleEntry> result = dialog.showAndWait();
        result.ifPresent(s -> { try { scheduleDAO.create(s); showAlert(Alert.AlertType.INFORMATION, "Success", "Schedule created."); loadScheduleData(); } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); } });
    }

    private void handleEditSchedule(ScheduleEntry schedule) {
        Dialog<ScheduleEntry> dialog = new Dialog<>();
        dialog.setTitle("Edit Schedule");
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TextField personIdField = new TextField(String.valueOf(schedule.getPersonID()));
        TextField sessionIdField = new TextField(String.valueOf(schedule.getSessionID()));
        TextField startTimeField = new TextField(schedule.getStartTime() != null ? schedule.getStartTime().format(fmt) : "");
        TextField endTimeField = new TextField(schedule.getEndTime() != null ? schedule.getEndTime().format(fmt) : "");

        grid.add(new Label("Person ID:"), 0, 0); grid.add(personIdField, 1, 0);
        grid.add(new Label("Session ID:"), 0, 1); grid.add(sessionIdField, 1, 1);
        grid.add(new Label("Start Time:"), 0, 2); grid.add(startTimeField, 1, 2);
        grid.add(new Label("End Time:"), 0, 3); grid.add(endTimeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButton) {
                try {
                    schedule.setPersonID(Integer.parseInt(personIdField.getText().trim()));
                    schedule.setSessionID(Integer.parseInt(sessionIdField.getText().trim()));
                    schedule.setStartTime(LocalDateTime.parse(startTimeField.getText().trim(), fmt));
                    schedule.setEndTime(LocalDateTime.parse(endTimeField.getText().trim(), fmt));
                    return schedule;
                } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid input: " + e.getMessage()); return null; }
            }
            return null;
        });

        Optional<ScheduleEntry> result = dialog.showAndWait();
        result.ifPresent(s -> { try { scheduleDAO.update(s); showAlert(Alert.AlertType.INFORMATION, "Success", "Schedule updated."); loadScheduleData(); } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); } });
    }

    private void handleDeleteSchedule(ScheduleEntry schedule) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Schedule");
        confirm.setHeaderText("Delete schedule ID: " + schedule.getId());
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try { scheduleDAO.delete(schedule.getId()); showAlert(Alert.AlertType.INFORMATION, "Success", "Schedule deleted."); loadScheduleData(); }
            catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed: " + e.getMessage()); }
        }
    }

    // ======================= TAB 7: REPORTS & STATISTICS =======================

    private VBox createStatisticsView() {
        VBox statsView = new VBox(15);
        statsView.setPadding(new Insets(20));

        Label sectionTitle = new Label("Reports & Statistics");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        HBox summaryCards = new HBox(20);
        summaryCards.setAlignment(Pos.CENTER);

        VBox totalEventsCard = createStatCard("Total Events", "0", "#3498db");
        totalEventsValue = (Label) totalEventsCard.getChildren().get(1);

        VBox totalUsersCard = createStatCard("Total Users", "0", "#27ae60");
        totalUsersValue = (Label) totalUsersCard.getChildren().get(1);

        VBox totalTicketsCard = createStatCard("Total Tickets", "0", "#9b59b6");
        totalTicketsValue = (Label) totalTicketsCard.getChildren().get(1);

        VBox totalRevenueCard = createStatCard("Total Revenue", "$0.00", "#e74c3c");
        totalRevenueValue = (Label) totalRevenueCard.getChildren().get(1);

        summaryCards.getChildren().addAll(totalEventsCard, totalUsersCard, totalTicketsCard, totalRevenueCard);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> loadStatisticsData());

        statsTable = new TableView<>();
        statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(statsTable, Priority.ALWAYS);

        TableColumn<EventStatistics, Integer> eventIdCol = new TableColumn<>("Event ID");
        eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        eventIdCol.setPrefWidth(80);

        TableColumn<EventStatistics, String> eventNameCol = new TableColumn<>("Event Name");
        eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventNameCol.setPrefWidth(200);

        TableColumn<EventStatistics, Number> revenueCol = new TableColumn<>("Revenue ($)");
        revenueCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalRevenue()));
        revenueCol.setPrefWidth(120);

        TableColumn<EventStatistics, Number> ticketsSoldCol = new TableColumn<>("Tickets Sold");
        ticketsSoldCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getTotalTicketsSold()));
        ticketsSoldCol.setPrefWidth(100);

        TableColumn<EventStatistics, String> attendanceCol = new TableColumn<>("Attendance Rate");
        attendanceCol.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f%%", cell.getValue().getAttendanceRate())));
        attendanceCol.setPrefWidth(120);

        statsTable.getColumns().addAll(eventIdCol, eventNameCol, revenueCol, ticketsSoldCol, attendanceCol);
        statsView.getChildren().addAll(sectionTitle, summaryCards, refreshBtn, statsTable);
        return statsView;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(180);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void loadStatisticsData() {
        // Use BulkDataLoader for optimized single-connection loading
        Thread loadThread = new Thread(() -> {
            try {
                BulkDataLoader.SystemAdminData data = BulkDataLoader.loadSystemAdminData();
                double revenue = data.tickets.stream().mapToDouble(Ticket::getPrice).sum();
                
                Platform.runLater(() -> {
                    totalEventsValue.setText(String.valueOf(data.events.size()));
                    totalUsersValue.setText(String.valueOf(data.users.size()));
                    totalTicketsValue.setText(String.valueOf(data.tickets.size()));
                    totalRevenueValue.setText(String.format("$%.2f", revenue));
                    statsTable.setItems(FXCollections.observableArrayList(data.statistics));
                });
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // ======================= UTILITY METHODS =======================

    private void handleLogout() {
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
