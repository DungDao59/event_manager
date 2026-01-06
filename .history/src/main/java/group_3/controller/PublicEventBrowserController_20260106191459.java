package group_3.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import group_3.dao.EventDAO;
import group_3.dao.PresenterDAO;
import group_3.dao.SessionDAO;
import group_3.model.Event;
import group_3.model.Presenter;
import group_3.model.Session;
import group_3.model.enums.EventType;
import group_3.util.DaoProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Controller for Public Event Browser (Anonymous Visitors).
 * Allows visitors to browse events and view presenter profiles without authentication.
 * 
 * Visitors can:
 * - View all available events
 * - Filter by event type, date, location, availability
 * - View presenter profiles (restricted info)
 * 
 * Visitors CANNOT:
 * - Register for events/sessions
 * - Access administrative interfaces
 * - View internal records
 * 
 * @author Group 3
 */
public class PublicEventBrowserController {

    private final EventDAO eventDAO;
    private final PresenterDAO presenterDAO;
    private final SessionDAO sessionDAO;
    
    private Scene scene;
    private ObservableList<Event> eventList;
    private ObservableList<Event> filteredList;

    // UI Components
    private TableView<Event> eventTable;
    private TextField searchField;
    private ComboBox<String> typeFilterCombo;
    private ComboBox<String> statusFilterCombo;
    private DatePicker dateFilterPicker;
    private TextField locationFilterField;
    private Label eventCountLabel;

    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PublicEventBrowserController() {
        this.eventDAO = DaoProvider.getEventDAO();
        this.presenterDAO = DaoProvider.getPresenterDAO();
        this.sessionDAO = DaoProvider.getSessionDAO();
        this.eventList = FXCollections.observableArrayList();
        this.filteredList = FXCollections.observableArrayList();
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-color: #f5f5f5;");

        // Header
        root.setTop(createHeader());

        // Main Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(createFilterSection(), createEventTable());
        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(content);

        // Footer
        root.setBottom(createFooter());

        scene = new Scene(root);
        loadEvents();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("🎪 Event Discovery");
        titleLabel.setFont(new Font("System Bold", 28));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label("Browse upcoming events and presenters");
        subtitleLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");

        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25;");
        loginBtn.setOnAction(e -> navigateToLogin());

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 25;");
        registerBtn.setOnAction(e -> navigateToRegister());

        header.getChildren().addAll(titleBox, spacer, loginBtn, registerBtn);
        return header;
    }

    private VBox createFilterSection() {
        VBox filterBox = new VBox(15);
        filterBox.setPadding(new Insets(20));
        filterBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label filterTitle = new Label("🔍 Filter Events");
        filterTitle.setFont(new Font("System Bold", 16));
        filterTitle.setStyle("-fx-text-fill: #2c3e50;");

        // First Row - Search and Type
        HBox row1 = new HBox(15);
        row1.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search by event name...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-padding: 10;");
        searchField.setOnAction(e -> applyFilters());

        typeFilterCombo = new ComboBox<>();
        typeFilterCombo.setPromptText("Event Type");
        typeFilterCombo.getItems().add("All Types");
        for (EventType type : EventType.values()) {
            typeFilterCombo.getItems().add(type.toString());
        }
        typeFilterCombo.setValue("All Types");
        typeFilterCombo.setPrefWidth(150);
        typeFilterCombo.setOnAction(e -> applyFilters());

        statusFilterCombo = new ComboBox<>();
        statusFilterCombo.setPromptText("Status");
        statusFilterCombo.getItems().addAll("All Statuses", "UPCOMING", "ONGOING", "COMPLETED", "CANCELLED");
        statusFilterCombo.setValue("All Statuses");
        statusFilterCombo.setPrefWidth(150);
        statusFilterCombo.setOnAction(e -> applyFilters());

        row1.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Type:"), typeFilterCombo,
            new Label("Status:"), statusFilterCombo
        );

        // Second Row - Date and Location
        HBox row2 = new HBox(15);
        row2.setAlignment(Pos.CENTER_LEFT);

        dateFilterPicker = new DatePicker();
        dateFilterPicker.setPromptText("Filter by date");
        dateFilterPicker.setPrefWidth(150);
        dateFilterPicker.setOnAction(e -> applyFilters());

        locationFilterField = new TextField();
        locationFilterField.setPromptText("Filter by location...");
        locationFilterField.setPrefWidth(200);
        locationFilterField.setStyle("-fx-padding: 10;");
        locationFilterField.setOnAction(e -> applyFilters());

        Button applyBtn = new Button("Apply Filters");
        applyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20;");
        applyBtn.setOnAction(e -> applyFilters());

        Button clearBtn = new Button("Clear All");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20;");
        clearBtn.setOnAction(e -> clearFilters());

        row2.getChildren().addAll(
            new Label("Date:"), dateFilterPicker,
            new Label("Location:"), locationFilterField,
            applyBtn, clearBtn
        );

        filterBox.getChildren().addAll(filterTitle, row1, row2);
        return filterBox;
    }

    private VBox createEventTable() {
        VBox tableBox = new VBox(10);
        tableBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        tableBox.setPadding(new Insets(15));

        Label tableTitle = new Label("📅 Available Events");
        tableTitle.setFont(new Font("System Bold", 16));
        tableTitle.setStyle("-fx-text-fill: #2c3e50;");

        eventTable = new TableView<>();
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        eventTable.setItems(filteredList);
        VBox.setVgrow(eventTable, Priority.ALWAYS);

        // Event Name Column
        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        nameCol.setPrefWidth(200);

        // Type Column
        TableColumn<Event, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getType().toString()));
        typeCol.setPrefWidth(100);

        // Location Column
        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getLocation()));
        locationCol.setPrefWidth(150);

        // Date Column
        TableColumn<Event, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cell -> {
            Event event = cell.getValue();
            if (event.getStartDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    event.getStartDate().format(DATE_FORMATTER));
            }
            return new javafx.beans.property.SimpleStringProperty("TBA");
        });
        dateCol.setPrefWidth(150);

        // Status Column with color coding
        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().toString()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "UPCOMING" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        case "ONGOING" -> setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        case "COMPLETED" -> setStyle("-fx-text-fill: #7f8c8d;");
                        case "CANCELLED" -> setStyle("-fx-text-fill: #e74c3c;");
                        default -> setStyle("");
                    }
                }
            }
        });
        statusCol.setPrefWidth(100);

        // Actions Column
        TableColumn<Event, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(180);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View Details");
            private final Button presentersBtn = new Button("Presenters");
            private final HBox buttons = new HBox(5, viewBtn, presentersBtn);

            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10;");
                presentersBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 5 10;");

                viewBtn.setOnAction(e -> showEventDetails(getTableRow().getItem()));
                presentersBtn.setOnAction(e -> showEventPresenters(getTableRow().getItem()));

                buttons.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        eventTable.getColumns().addAll(nameCol, typeCol, locationCol, dateCol, statusCol, actionsCol);

        tableBox.getChildren().addAll(tableTitle, eventTable);
        return tableBox;
    }

    private HBox createFooter() {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(15, 30, 15, 30));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");

        eventCountLabel = new Label("Showing 0 events");
        eventCountLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label infoLabel = new Label("💡 Sign in to register for events and sessions");
        infoLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        footer.getChildren().addAll(eventCountLabel, spacer, infoLabel);
        return footer;
    }

    private void loadEvents() {
        try {
            List<Event> events = eventDAO.findAll();
            eventList.setAll(events);
            applyFilters();
        } catch (Exception e) {
            showError("Error loading events: " + e.getMessage());
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedType = typeFilterCombo.getValue();
        String selectedStatus = statusFilterCombo.getValue();
        LocalDate selectedDate = dateFilterPicker.getValue();
        String locationText = locationFilterField.getText().toLowerCase().trim();

        filteredList.clear();

        for (Event event : eventList) {
            boolean matchesSearch = searchText.isEmpty() ||
                event.getName().toLowerCase().contains(searchText);

            boolean matchesType = "All Types".equals(selectedType) || selectedType == null ||
                event.getType().toString().equals(selectedType);

            boolean matchesStatus = "All Statuses".equals(selectedStatus) || selectedStatus == null ||
                event.getStatus().toString().equals(selectedStatus);

            boolean matchesDate = selectedDate == null ||
                (event.getStartDate() != null && 
                 event.getStartDate().toLocalDate().equals(selectedDate));

            boolean matchesLocation = locationText.isEmpty() ||
                event.getLocation().toLowerCase().contains(locationText);

            if (matchesSearch && matchesType && matchesStatus && matchesDate && matchesLocation) {
                filteredList.add(event);
            }
        }

        eventCountLabel.setText("Showing " + filteredList.size() + " events");
    }

    private void clearFilters() {
        searchField.clear();
        typeFilterCombo.setValue("All Types");
        statusFilterCombo.setValue("All Statuses");
        dateFilterPicker.setValue(null);
        locationFilterField.clear();
        applyFilters();
    }

    private void showEventDetails(Event event) {
        if (event == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Details");
        dialog.setHeaderText(event.getName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);

        // Event Info Grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        int row = 0;
        addInfoRow(grid, row++, "Type:", event.getType().toString());
        addInfoRow(grid, row++, "Location:", event.getLocation());
        addInfoRow(grid, row++, "Status:", event.getStatus().toString());
        
        if (event.getStartDate() != null) {
            addInfoRow(grid, row++, "Start:", event.getStartDate().format(DATE_FORMATTER));
        }
        if (event.getEndDate() != null) {
            addInfoRow(grid, row++, "End:", event.getEndDate().format(DATE_FORMATTER));
        }
        addInfoRow(grid, row++, "Duration:", event.getDuration() + " minutes");

        content.getChildren().add(grid);

        // Sessions Preview
        List<Session> sessions = sessionDAO.findByEventId(event.getEventId());
        if (!sessions.isEmpty()) {
            Label sessionsTitle = new Label("📋 Sessions");
            sessionsTitle.setFont(new Font("System Bold", 14));

            VBox sessionsList = new VBox(5);
            for (Session session : sessions) {
                Label sessionLabel = new Label(session.getTitle());
                sessionLabel.setStyle("-fx-text-fill: #34495e;");
                sessionsList.getChildren().add(sessionLabel);
            }

            content.getChildren().addAll(new Separator(), sessionsTitle, sessionsList);
        }

        // Info about registration
        Label registerInfo = new Label("ℹ️ Sign in to register for this event");
        registerInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        content.getChildren().addAll(new Separator(), registerInfo);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showEventPresenters(Event event) {
        if (event == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Event Presenters");
        dialog.setHeaderText("Presenters for: " + event.getName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);

        // Get sessions and their presenters
        List<Session> sessions = sessionDAO.findByEventId(event.getEventId());
        
        if (sessions.isEmpty()) {
            content.getChildren().add(new Label("No sessions scheduled yet."));
        } else {
            for (Session session : sessions) {
                VBox sessionBox = new VBox(8);
                sessionBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");

                Label sessionTitle = new Label("📌 " + session.getTitle());
                sessionTitle.setFont(new Font("System Bold", 13));
                sessionBox.getChildren().add(sessionTitle);

                // Get presenter IDs from session
                List<Integer> presenterIds = session.getPresenterIds();
                if (presenterIds != null && !presenterIds.isEmpty()) {
                    for (Integer presenterId : presenterIds) {
                        Optional<Presenter> presenterOpt = presenterDAO.findById(presenterId);
                        if (presenterOpt.isPresent()) {
                            Presenter presenter = presenterOpt.get();
                            HBox presenterRow = createPresenterRow(presenter);
                            sessionBox.getChildren().add(presenterRow);
                        }
                    }
                } else {
                    sessionBox.getChildren().add(new Label("   No presenters assigned"));
                }

                content.getChildren().add(sessionBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Creates a presenter row with RESTRICTED information (public view).
     * Does NOT show: username, password, contact info, statistics
     * Shows: Full name, presenter role
     */
    private HBox createPresenterRow(Presenter presenter) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 10, 5, 20));

        Label nameLabel = new Label("👤 " + presenter.getFullName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label roleLabel = new Label("(" + presenter.getPresenterRole() + ")");
        roleLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button viewProfileBtn = new Button("View Profile");
        viewProfileBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 11px;");
        viewProfileBtn.setOnAction(e -> showPresenterProfile(presenter));

        row.getChildren().addAll(nameLabel, roleLabel, viewProfileBtn);
        return row;
    }

    /**
     * Shows presenter profile with RESTRICTED information only.
     * Public visitors cannot see: username, email, phone, statistics, internal records
     */
    private void showPresenterProfile(Presenter presenter) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Presenter Profile");
        dialog.setHeaderText(presenter.getFullName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        // RESTRICTED INFO ONLY - No sensitive data
        int row = 0;
        addInfoRow(grid, row++, "Name:", presenter.getFullName());
        addInfoRow(grid, row++, "Role:", presenter.getPresenterRole());
        
        // Do NOT show:
        // - Username
        // - Email/Contact info
        // - Date of birth
        // - Statistics/Performance data

        content.getChildren().add(grid);

        // Privacy notice
        Label privacyLabel = new Label("ℹ️ Contact information is only available to registered users.");
        privacyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-wrap-text: true;");
        privacyLabel.setWrapText(true);

        content.getChildren().addAll(new Separator(), privacyLabel);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #2c3e50;");
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void navigateToLogin() {
        try {
            Stage stage = (Stage) scene.getWindow();
            LoginController loginController = new LoginController();
            Scene loginScene = loginController.getScene();
            if (loginScene != null) {
                stage.setScene(loginScene);
                stage.setTitle("Login - Event Management System");
                stage.setWidth(500);
                stage.setHeight(600);
            }
        } catch (Exception e) {
            showError("Could not load login page");
        }
    }

    private void navigateToRegister() {
        try {
            Stage stage = (Stage) scene.getWindow();
            RegistrationController regController = new RegistrationController();
            Scene regScene = regController.getScene();
            if (regScene != null) {
                stage.setScene(regScene);
                stage.setTitle("Register - Event Management System");
                stage.setWidth(550);
                stage.setHeight(700);
            }
        } catch (Exception e) {
            showError("Could not load registration page");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
