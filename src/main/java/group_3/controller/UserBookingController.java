package group_3.controller;

import group_3.model.Event;
import group_3.model.Session;
import group_3.model.enums.TicketType;
import group_3.service.EventAdminService.EventAdminService;
import group_3.service.EventAdminService.EventAdminServiceImpl;
import group_3.service.RegistrationService.RegistrationService;
import group_3.service.RegistrationService.RegistrationServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserBookingController {
    private final EventAdminService eventService = new EventAdminServiceImpl();
    private final EventAdminService sessionService = new EventAdminServiceImpl();
    private final RegistrationService registrationService = new RegistrationServiceImpl();

    private final int currentUserId = 1;
    private ComboBox<TicketType> ticketTypeCombo;

    private Stage stage;
    private TableView<Event> eventTable;
    private TableView<Session> sessionTable;
    private Button registerBtn;
    private Label statusLabel;

    public void show() {
        stage = new Stage();
        stage.setTitle("Booking System");
        stage.setScene(createScene());
        stage.setWidth(1500);
        stage.setHeight(1000);
        stage.show();
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();

        //header
        Label header = new Label("Event Booking System");
        header.setFont(new Font("System Bold", 24));
        root.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 15, 0));

        //Left: events list
        VBox leftBox = new VBox(10);
        Label eventLabel = new Label("1. Select an Event");
        eventLabel.setFont(new Font("System Bold", 14));

        eventTable = new TableView<>();
        setupEventTable();
        VBox.setVgrow(eventTable, Priority.ALWAYS);

        leftBox.getChildren().addAll(eventLabel, eventTable);

        //Right: session list
        VBox rightBox = new VBox(10);
        Label sessionLabel = new Label("2. Select an Session");
        sessionLabel.setFont(new Font("System Bold", 14));

        sessionTable = new TableView<>();
        setupSessionTable();
        VBox.setVgrow(sessionTable, Priority.ALWAYS);

        Label typeLabel = new Label("3. Choose Ticket Type");
        typeLabel.setFont(new Font("System Bold", 14));

        ticketTypeCombo = new ComboBox<>();
        ticketTypeCombo.getItems().setAll(TicketType.values());
        ticketTypeCombo.getSelectionModel().select(0); //default selection
        ticketTypeCombo.setMaxWidth(Double.MAX_VALUE);

        registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e -> handleRegister());

        Button myTicketsBtn = new Button("View My Tickets");
        myTicketsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        myTicketsBtn.setMaxWidth(Double.MAX_VALUE);
        myTicketsBtn.setOnAction(e -> new TicketsController().show());

        statusLabel = new Label("");

        rightBox.getChildren().addAll(
                sessionLabel, sessionTable,
                new Separator(),
                typeLabel, ticketTypeCombo, // Added here
                registerBtn, myTicketsBtn, statusLabel
        );

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftBox, rightBox);
        splitPane.setDividerPositions(0.65);

        root.setCenter(splitPane);
        loadEvents();

        return new Scene(root);
    }

    private void setupEventTable() {
        TableColumn<Event, String> colTitle = new TableColumn<>("Event Name");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTitle.setPrefWidth(150);

        TableColumn<Event, String> colStartDate = new TableColumn<>("Start Date");
        colStartDate.setCellValueFactory(cell -> {
            if (cell.getValue().getStartDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colStartDate.setPrefWidth(100);

        TableColumn<Event, String> colEndDate = new TableColumn<>("End Date");
        colEndDate.setCellValueFactory(cell -> {
            if (cell.getValue().getEndDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colEndDate.setPrefWidth(100);

        TableColumn<Event, String> colDuration = new TableColumn<>("Duration");
        colDuration.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getDuration() + " days")
        );
        colDuration.setPrefWidth(70);


        TableColumn<Event, String> colLoc = new TableColumn<>("Location");
        colLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        colLoc.setPrefWidth(100);

        eventTable.getColumns().addAll(colTitle, colStartDate, colEndDate, colLoc, colDuration);

        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //remove blank column

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newEvent) -> {
            if (newEvent != null) {
                loadSessions(newEvent.getEventId());
            }
        });
    }

    private void setupSessionTable() {
        // Session Name
        TableColumn<Session, String> colName = new TableColumn<>("Session Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setPrefWidth(150);

        // Venue
        TableColumn<Session, String> colVenue = new TableColumn<>("Venue");
        colVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colVenue.setPrefWidth(100);


        // Start Time
        TableColumn<Session, String> colStart = new TableColumn<>("Start Time");
        colStart.setCellValueFactory(cell -> {
            if (cell.getValue().getStartTime() != null) {
                return new SimpleStringProperty(cell.getValue().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        colStart.setPrefWidth(80);

        // End Time
        TableColumn<Session, String> colEnd = new TableColumn<>("End Time");
        colEnd.setCellValueFactory(cell -> {
            if (cell.getValue().getEndTime() != null) {
                return new SimpleStringProperty(cell.getValue().getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        colEnd.setPrefWidth(80);

        // Description
        TableColumn<Session, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(200);

        // Add all columns to the table
        sessionTable.getColumns().setAll(colName, colVenue, colStart, colEnd, colDesc);

        // --- MATCHING STYLE: Remove the blank extra column ---
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }



    private void loadEvents() {
        eventTable.getItems().setAll(eventService.getAllEvents());
    }

    private void loadSessions(int eventId) {
        sessionTable.getItems().clear();
        List<Session> sessions = sessionService.getSessionsByEventId(eventId);
        if (sessions.isEmpty()) {
            statusLabel.setText("No sessions found.");
        } else {
            sessionTable.getItems().addAll(sessions);
            statusLabel.setText("Found " + sessions.size() + " sessions.");
        }
    }

    private void handleRegister() {
        Session selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        TicketType selectedType = ticketTypeCombo.getValue();

        if (selectedSession == null) {
            showAlert(Alert.AlertType.WARNING, "No Session", "Please select a session.");
            return;
        }

        if (selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "No Type", "Please select a ticket type.");
            return;
        }


        double price = 50.00; // Default Price for Standard
        if (selectedType.toString().equalsIgnoreCase("VIP")) {
            price = 100.00;
        }

        boolean success = registrationService.registerAttendee(
                currentUserId,
                selectedSession.getSessionId(),
                selectedType,
                price
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registered as " + selectedType + "!");
            statusLabel.setText("Registered: " + selectedType);
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Conflict or Error.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
