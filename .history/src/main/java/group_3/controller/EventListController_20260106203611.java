package group_3.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import group_3.dao.EventDAO;
import group_3.model.Event;
import group_3.model.Person;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.security.AuthContext;
import group_3.util.DaoProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Controller for Event List View (Pure JavaFX Implementation).
 * Displays events in a table with search, filter, and CRUD operations.
 */
public class EventListController {
    
    private EventDAO eventDAO;
    private ObservableList<Event> eventList;
    private ObservableList<Event> filteredList;
    private Scene scene;
    
    // UI Components
    private TableView<Event> eventTable;
    private TextField searchField;
    private ComboBox<EventStatus> statusFilterCombo;
    private ComboBox<EventType> typeFilterCombo;
    private Label statusLabel;
    private Label eventCountLabel;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public EventListController() {
        eventDAO = DaoProvider.getEventDAO();
        eventList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        initializeUI();
    }
    
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        
        root.setTop(createTopSection());
        root.setCenter(createTableSection());
        root.setBottom(createStatusBar());
        
        scene = new Scene(root);
        loadEvents();
    }
    
    private VBox createTopSection() {
        VBox top = new VBox(10);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
        
        // Header Bar with user info
        HBox headerBar = new HBox(10);
        headerBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        headerBar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10px;");
        
        Label titleLabel = new Label("Event Management");
        titleLabel.setFont(new Font("System Bold", 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        // Spacer to push user info to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Current user info - show just the username
        Person currentUser = AuthContext.getCurrentUser();
        String username = currentUser != null ? currentUser.getUsername() : "Guest";
        
        Label userLabel = new Label(username);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Button profileBtn = createStyledButton("My Profile", "#27ae60");
        profileBtn.setOnAction(e -> handleMyProfile());
        
        Button logoutBtn = createStyledButton("Logout", "#e74c3c");
        logoutBtn.setOnAction(e -> handleLogout());
        
        headerBar.getChildren().addAll(titleLabel, spacer, userLabel, profileBtn, logoutBtn);
        
        // Title Bar with action buttons
        HBox titleBar = new HBox(10);
        titleBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button createBtn = createStyledButton("+ Create Event", "#3498db");
        createBtn.setOnAction(e -> handleCreateEvent());
        
        Button refreshBtn = createStyledButton("Refresh", "#95a5a6");
        refreshBtn.setOnAction(e -> handleRefresh());
        
        titleBar.getChildren().addAll(createBtn, refreshBtn);
        
        // Filter Bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        searchField = new TextField();
        searchField.setPromptText("Search events...");
        searchField.setPrefWidth(300);
        searchField.setOnAction(e -> handleSearch());
        
        Button searchBtn = createStyledButton("Search", "#95a5a6");
        searchBtn.setOnAction(e -> handleSearch());
        
        statusFilterCombo = new ComboBox<>();
        statusFilterCombo.getItems().add(null);
        statusFilterCombo.getItems().addAll(EventStatus.values());
        statusFilterCombo.setPrefWidth(150);
        statusFilterCombo.setPromptText("All Statuses");
        statusFilterCombo.setOnAction(e -> handleFilter());
        
        typeFilterCombo = new ComboBox<>();
        typeFilterCombo.getItems().add(null);
        typeFilterCombo.getItems().addAll(EventType.values());
        typeFilterCombo.setPrefWidth(150);
        typeFilterCombo.setPromptText("All Types");
        typeFilterCombo.setOnAction(e -> handleFilter());
        
        Button clearBtn = createStyledButton("Clear Filters", "#95a5a6");
        clearBtn.setOnAction(e -> handleClearFilters());
        
        filterBar.getChildren().addAll(
            searchField, searchBtn,
            new Label("Status:"), statusFilterCombo,
            new Label("Type:"), typeFilterCombo,
            clearBtn
        );
        
        top.getChildren().addAll(headerBar, titleBar, filterBar);
        return top;
    }
    
    private VBox createTableSection() {
        VBox center = new VBox();
        VBox.setVgrow(center, Priority.ALWAYS);
        
        eventTable = new TableView<>();
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        // Columns
        TableColumn<Event, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        idCol.setPrefWidth(80);
        
        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Event, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().toString()));
        typeCol.setPrefWidth(120);
        
        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);
        
        TableColumn<Event, String> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate() != null 
                    ? cellData.getValue().getStartDate().format(DATE_FORMATTER) 
                    : "N/A"));
        startDateCol.setPrefWidth(150);
        
        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        statusCol.setPrefWidth(100);
        
        TableColumn<Event, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5px 15px;");
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5px 15px;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5px 15px;");
                
                viewBtn.setOnAction(event -> handleViewEvent(getTableRow().getItem()));
                editBtn.setOnAction(event -> handleEditEvent(getTableRow().getItem()));
                deleteBtn.setOnAction(event -> handleDeleteEvent(getTableRow().getItem()));
                
                buttons.setStyle("-fx-alignment: center;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        @SuppressWarnings("unchecked")
        TableColumn<Event, ?>[] columns = new TableColumn[] {idCol, nameCol, typeCol, locationCol, startDateCol, statusCol, actionsCol};
        eventTable.getColumns().addAll(columns);
        eventTable.setItems(filteredList);
        eventTable.setStyle("-fx-font-size: 13px;");
        
        VBox.setVgrow(eventTable, Priority.ALWAYS);
        center.getChildren().add(eventTable);
        
        return center;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10, 20, 10, 20));
        statusBar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bbb; -fx-border-width: 1 0 0 0;");
        statusBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        
        eventCountLabel = new Label("Total Events: 0");
        
        statusBar.getChildren().addAll(statusLabel, eventCountLabel);
        return statusBar;
    }
    
    private void loadEvents() {
        try {
            List<Event> events = eventDAO.findAll();
            eventList.setAll(events);
            applyFilters();
            updateStatus("Events loaded successfully", events.size());
        } catch (Exception e) {
            showError("Error loading events", e.getMessage());
        }
    }
    
    private void handleCreateEvent() {
        EventFormController formController = new EventFormController(null, this);
        formController.show();
    }
    
    private void handleViewEvent(Event event) {
        if (event == null) return;
        EventDetailController detailController = new EventDetailController(event, this);
        detailController.show();
    }
    
    private void handleEditEvent(Event event) {
        if (event == null) return;
        EventFormController formController = new EventFormController(event, this);
        formController.show();
    }
    
    private void handleDeleteEvent(Event event) {
        if (event == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Event: " + event.getName());
        alert.setContentText("Are you sure you want to delete this event?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int eventId = event.getEventId();
                eventDAO.delete(eventId);
                loadEvents();
                updateStatus("Event deleted successfully", filteredList.size());
            } catch (Exception e) {
                showError("Error deleting event", e.getMessage());
            }
        }
    }
    
    private void handleRefresh() {
        loadEvents();
    }
    
    private void handleSearch() {
        applyFilters();
    }
    
    private void handleFilter() {
        applyFilters();
    }
    
    private void handleClearFilters() {
        searchField.clear();
        statusFilterCombo.setValue(null);
        typeFilterCombo.setValue(null);
        applyFilters();
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        EventStatus selectedStatus = statusFilterCombo.getValue();
        EventType selectedType = typeFilterCombo.getValue();
        
        filteredList.clear();
        
        for (Event event : eventList) {
            boolean matchesSearch = searchText.isEmpty() || 
                event.getName().toLowerCase().contains(searchText) ||
                event.getLocation().toLowerCase().contains(searchText) ||
                String.valueOf(event.getEventId()).contains(searchText);
            
            boolean matchesStatus = selectedStatus == null || event.getStatus() == selectedStatus;
            boolean matchesType = selectedType == null || event.getType() == selectedType;
            
            if (matchesSearch && matchesStatus && matchesType) {
                filteredList.add(event);
            }
        }
        
        updateStatus("Filtered results", filteredList.size());
    }
    
    private void updateStatus(String message, int count) {
        statusLabel.setText(message);
        eventCountLabel.setText("Total Events: " + count);
    }
    
    private void handleMyProfile() {
        try {
            Stage stage = (Stage) eventTable.getScene().getWindow();
            ProfileController profileController = new ProfileController();
            stage.setScene(profileController.getScene());
            stage.setTitle("My Profile");
        } catch (Exception e) {
            showError("Navigation Error", "Could not load profile page: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        try {
            AuthContext.clear();
            Stage stage = (Stage) eventTable.getScene().getWindow();
            LoginController loginController = new LoginController();
            stage.setScene(loginController.getScene());
            stage.setTitle("Login - Event Management System");
            stage.setWidth(500);
            stage.setHeight(600);
        } catch (Exception e) {
            showError("Logout Error", "Could not logout: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        return btn;
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void refreshEvents() {
        loadEvents();
    }
}
