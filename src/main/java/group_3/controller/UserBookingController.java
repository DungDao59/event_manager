package group_3.controller;

import group_3.model.Event;
import group_3.model.Session;
import group_3.model.enums.TicketType;
import group_3.service.EventAdminService.EventAdminService;
import group_3.service.EventAdminService.EventAdminServiceImpl;
import group_3.service.RegistrationService.RegistrationService;
import group_3.service.RegistrationService.RegistrationServiceImpl;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBookingController {
    private final EventAdminService eventService = new EventAdminServiceImpl();
    private final EventAdminService sessionService = new EventAdminServiceImpl();
    private final RegistrationService registrationService = new RegistrationServiceImpl();

    private final int currentUserId = 1;
    private ComboBox<TicketType> ticketTypeCombo;

    private Stage stage;
    private TableView<Event> eventTable;
    private ListView<Session> sessionList;
    private Button registerBtn;
    private Label statusLabel;

    public void show() {
        stage = new Stage();
        stage.setTitle("Attendee Dashboard");
        stage.setScene(createScene());
        stage.setWidth(1000);
        stage.setHeight(600);
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

        sessionList = new ListView<>();
        VBox.setVgrow(sessionList, Priority.ALWAYS);

        rightBox.getChildren().addAll(sessionLabel, sessionList);

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
//        myTicketsBtn.setOnAction(e -> new MyTicketsController().show());

        rightBox.getChildren().addAll(
                sessionLabel, sessionList,
                new Separator(),
                typeLabel, ticketTypeCombo, // Added here
                registerBtn, myTicketsBtn, statusLabel
        );

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftBox, rightBox);
        splitPane.setDividerPositions(0.65);

        root.setCenter(splitPane);
//        loadEvents();

        return new Scene(root);
    }

    private void setupEventTable() {
        TableColumn<Event, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> colLoc = new TableColumn<>("Location");
        colLoc.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Event, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cell -> {
            if (cell.getValue().getStartDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        eventTable.getColumns().addAll(colTitle, colDate, colLoc);

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newEvent) -> {
            if (newEvent != null) {
//                loadSessions(newEvent.getEventId());
            }
        });
    }

    private void loadEvents() {
        eventTable.getItems().setAll(eventService.getAllEvents());
    }

    private void loadSessions(int eventId) {
        sessionList.getItems().clear();
        List<Session> sessions = sessionService.getSessionsByEventId(eventId);
        if (sessions.isEmpty()) {
            statusLabel.setText("No sessions found.");
        } else {
            sessionList.getItems().addAll(sessions);
            statusLabel.setText("Found " + sessions.size() + " sessions.");
        }
    }

    private void handleRegister() {
        Session selectedSession = sessionList.getSelectionModel().getSelectedItem();
        // 3. GET SELECTED TYPE
        TicketType selectedType = ticketTypeCombo.getValue();

        if (selectedSession == null) {
            showAlert(Alert.AlertType.WARNING, "No Session", "Please select a session.");
            return;
        }

        if (selectedType == null) {
            showAlert(Alert.AlertType.WARNING, "No Type", "Please select a ticket type.");
            return;
        }

        // 4. CALCULATE PRICE (Simple Logic)
        // You can make this complex later (e.g., fetch from DB)
        double price = 50.00; // Default Standard Price
        if (selectedType.toString().equalsIgnoreCase("VIP")) {
            price = 100.00;
        }

        boolean success = registrationService.registerAttendee(
                currentUserId,
                selectedSession.getSessionId(),
                selectedType, // Pass the user's choice here!
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
