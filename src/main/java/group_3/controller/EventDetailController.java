package group_3.controller;

import group_3.dao.EventDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.util.DaoProvider;
import group_3.model.Event;
import group_3.model.EventStatistics;
import group_3.service.EventStatisticsService.EventStatisticsService;
import group_3.service.EventStatisticsService.EventStatisticsServiceImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for Event Detail View.
 * Pure JavaFX implementation without FXML.
 */
public class EventDetailController {
    
    private Stage stage;
    private Event currentEvent;
    private EventListController listController;
    private EventDAO eventDAO;
    private EventStatisticsService statisticsService;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm");
    
    public EventDetailController(Event event, EventListController listController) {
        this.currentEvent = event;
        this.listController = listController;
        this.eventDAO = DaoProvider.getEventDAO();

        SessionDAO sessionDAO = DaoProvider.getSessionDAO();
        TicketDAO ticketDAO = DaoProvider.getTicketDAO();
        this.statisticsService = new EventStatisticsServiceImpl(eventDAO, sessionDAO, ticketDAO);
    }
    
    public void show() {
        stage = new Stage();
        stage.setTitle("Event Details - " + currentEvent.getName());
        stage.setScene(createScene());
        stage.setWidth(950);
        stage.setHeight(800);
        stage.show();
    }
    
    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-color: white;");
        
        root.setTop(createHeader());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        scrollPane.setContent(createContent());
        root.setCenter(scrollPane);
        
        return new Scene(root);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");
        
        Button backBtn = createButton("← Back", "#95a5a6");
        backBtn.setOnAction(e -> stage.close());
        
        Label title = new Label(currentEvent.getName());
        title.setFont(new Font("System Bold", 24));
        HBox.setHgrow(title, Priority.ALWAYS);
        
        Button editBtn = createButton("Edit Event", "#3498db");
        editBtn.setOnAction(e -> handleEdit());
        
        Button deleteBtn = createButton("Delete Event", "#e74c3c");
        deleteBtn.setOnAction(e -> handleDelete());
        
        header.getChildren().addAll(backBtn, title, editBtn, deleteBtn);
        return header;
    }
    
    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 40, 20, 40));
        
        content.getChildren().add(createImageSection());
        content.getChildren().add(createBasicInfoSection());
        content.getChildren().add(createScheduleSection());
        content.getChildren().add(createSessionsSection());
        content.getChildren().add(createStatisticsSection());
        
        return content;
    }
    
    private VBox createImageSection() {
        VBox section = new VBox(10);
        section.setAlignment(javafx.geometry.Pos.CENTER);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        section.setPrefHeight(350);
        
        if (currentEvent.getEventImage() != null && !currentEvent.getEventImage().isEmpty()) {
            try {
                Image image = new Image(currentEvent.getEventImage(), 600, 300, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(600);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                section.getChildren().add(imageView);
            } catch (Exception e) {
                Label placeholder = new Label("Image not available");
                placeholder.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");
                section.getChildren().add(placeholder);
            }
        } else {
            Label placeholder = new Label("📷 No Event Image");
            placeholder.setStyle("-fx-text-fill: #bbb; -fx-font-size: 18px; -fx-font-weight: bold;");
            section.getChildren().add(placeholder);
        }
        
        return section;
    }
    
    private VBox createBasicInfoSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Basic Information");
        title.setFont(new Font("System Bold", 18));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        
        grid.add(createDetailLabel("Event ID:"), 0, 0);
        grid.add(createValueLabel(currentEvent.getEventId()), 1, 0);
        
        grid.add(createDetailLabel("Event Name:"), 0, 1);
        grid.add(createValueLabel(currentEvent.getName()), 1, 1);
        
        grid.add(createDetailLabel("Type:"), 0, 2);
        grid.add(createValueLabel(currentEvent.getType().toString()), 1, 2);
        
        grid.add(createDetailLabel("Status:"), 0, 3);
        Label statusLabel = createValueLabel(currentEvent.getStatus().toString());
        applyStatusStyle(statusLabel, currentEvent.getStatus().toString());
        grid.add(statusLabel, 1, 3);
        
        section.getChildren().addAll(title, grid);
        return section;
    }
    
    private VBox createScheduleSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Schedule & Location");
        title.setFont(new Font("System Bold", 18));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        
        String startStr = currentEvent.getStartDate() != null 
            ? currentEvent.getStartDate().format(DATE_FORMATTER) 
            : "N/A";
        grid.add(createDetailLabel("Start Date:"), 0, 0);
        grid.add(createValueLabel(startStr), 1, 0);
        
        String endStr = currentEvent.getEndDate() != null 
            ? currentEvent.getEndDate().format(DATE_FORMATTER) 
            : "N/A";
        grid.add(createDetailLabel("End Date:"), 0, 1);
        grid.add(createValueLabel(endStr), 1, 1);
        
        grid.add(createDetailLabel("Duration:"), 0, 2);
        grid.add(createValueLabel(currentEvent.getDuration() + " day(s)"), 1, 2);
        
        grid.add(createDetailLabel("Location:"), 0, 3);
        grid.add(createValueLabel(currentEvent.getLocation()), 1, 3);
        
        section.getChildren().addAll(title, grid);
        return section;
    }
    
    private VBox createSessionsSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Associated Sessions");
        title.setFont(new Font("System Bold", 18));
        
        // Get SessionDAO to fetch session details
        SessionDAO sessionDAO = DaoProvider.getSessionDAO();
        
        // Build session display list with titles
        java.util.List<String> sessionDisplayList = currentEvent.getSessionIds().stream()
            .map(sessionId -> {
                try {
                    int sId = Integer.parseInt(sessionId);
                    return sessionDAO.findById(sId)
                        .map(s -> s.getTitle() + " (ID: " + sessionId + ")")
                        .orElse("Session #" + sessionId);
                } catch (Exception e) {
                    return "Session #" + sessionId;
                }
            })
            .collect(java.util.stream.Collectors.toList());
        
        ListView<String> sessionList = new ListView<>(
            FXCollections.observableArrayList(sessionDisplayList));
        sessionList.setPrefHeight(150);
        sessionList.setStyle("-fx-border-color: #bbb; -fx-border-radius: 3;");
        
        HBox buttons = new HBox(10);
        buttons.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button editBtn = createButton("View/Edit Session", "#3498db");
        editBtn.setOnAction(e -> {
            int idx = sessionList.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                String sessionId = currentEvent.getSessionIds().get(idx);
                try {
                    int sId = Integer.parseInt(sessionId);
                    java.util.Optional<group_3.model.Session> sessionOpt = 
                        sessionDAO.findById(sId);
                    if (sessionOpt.isPresent()) {
                        SessionEditorController editor = new SessionEditorController(sessionOpt.get(), this);
                        editor.show();
                    } else {
                        showError("Session Not Found", "Session with ID " + sessionId + " not found.");
                    }
                } catch (Exception ex) {
                    showError("Error", "Failed to open session editor: " + ex.getMessage());
                }
            } else {
                showError("No Selection", "Please select a session from the list first.");
            }
        });
        
        buttons.getChildren().addAll(editBtn);
        
        section.getChildren().addAll(title, sessionList, buttons);
        return section;
    }
    
    public void refreshSessions() {
        // Refresh logic if needed
    }
    
    private VBox createStatisticsSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Event Statistics");
        title.setFont(new Font("System Bold", 18));
        
        GridPane stats = new GridPane();
        stats.setHgap(30);
        stats.setVgap(15);
        stats.setStyle("-fx-padding: 15;");
        
        try {
            int eventId = Integer.parseInt(currentEvent.getEventId());
            Optional<EventStatistics> statsOpt = statisticsService.getEventStatistics(eventId);
            
            if (statsOpt.isPresent()) {
                EventStatistics stat = statsOpt.get();
                
                VBox revenueCard = createStatCard("Total Revenue", "$" + String.format("%.2f", stat.getTotalRevenue()));
                VBox ticketsCard = createStatCard("Tickets Sold", String.valueOf(stat.getTotalTicketsSold()));
                VBox attendanceCard = createStatCard("Attendance Rate", String.format("%.1f%%", stat.getAttendanceRate()));
                
                stats.add(revenueCard, 0, 0);
                stats.add(ticketsCard, 1, 0);
                stats.add(attendanceCard, 2, 0);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Statistics not available");
            errorLabel.setStyle("-fx-text-fill: #999;");
            stats.add(errorLabel, 0, 0);
        }
        
        Button viewStatsBtn = createButton("View Full Statistics", "#3498db");
        viewStatsBtn.setOnAction(e -> handleViewFullStatistics());
        
        section.getChildren().addAll(title, stats, viewStatsBtn);
        return section;
    }
    
    private VBox createStatCard(String label, String value) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; " +
                     "-fx-border-width: 1; -fx-border-radius: 5;");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(labelLbl, valueLbl);
        return card;
    }
    
    private Label createDetailLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        label.setPrefWidth(150);
        return label;
    }
    
    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #2c3e50;");
        label.setWrapText(true);
        return label;
    }
    
    private void applyStatusStyle(Label label, String status) {
        String bgColor;
        switch (status.toUpperCase()) {
            case "SCHEDULED":
                bgColor = "#3498db";
                break;
            case "ONGOING":
                bgColor = "#f39c12";
                break;
            case "COMPLETED":
                bgColor = "#27ae60";
                break;
            case "CANCELLED":
                bgColor = "#e74c3c";
                break;
            default:
                bgColor = "#95a5a6";
        }
        label.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                      "-fx-padding: 5px 10px; -fx-border-radius: 3; -fx-font-weight: bold;");
    }
    
    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-padding: 8px 20px; -fx-font-weight: bold;");
        return btn;
    }
    
    private void handleEdit() {
        EventFormController formController = new EventFormController(currentEvent, listController);
        formController.show();
        stage.close();
    }
    
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Event: " + currentEvent.getName());
        alert.setContentText("Are you sure? This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int eventId = Integer.parseInt(currentEvent.getEventId());
                eventDAO.delete(eventId);
                
                if (listController != null) {
                    listController.refreshEvents();
                }
                
                stage.close();
            } catch (Exception e) {
                showError("Error", e.getMessage());
            }
        }
    }
    
    private void handleViewSession() {
        showInfo("View Session", "Session detail view not yet implemented.");
    }
    
    private void handleManageSessions() {
        showInfo("Manage Sessions", "Session management view not yet implemented.");
    }
    
    private void handleViewFullStatistics() {
        try {
            int eventId = Integer.parseInt(currentEvent.getEventId());
            Optional<EventStatistics> statsOpt = statisticsService.getEventStatistics(eventId);
            
            if (statsOpt.isPresent()) {
                EventStatistics stat = statsOpt.get();
                String message = String.format(
                    "Event: %s\nRevenue: $%.2f\nTickets Sold: %d\nChecked In: %d\nAttendance Rate: %.1f%%",
                    stat.getEventName(),
                    stat.getTotalRevenue(),
                    stat.getTotalTicketsSold(),
                    stat.getTotalCheckedIn(),
                    stat.getAttendanceRate()
                );
                showInfo("Event Statistics", message);
            }
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
