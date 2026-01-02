package group_3.controller;

import group_3.dao.PresenterDAO;
import group_3.model.Presenter;
import group_3.model.Session;
import group_3.util.DaoProvider;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Session Editor with Presenter Assignment.
 * Allows editing session details and assigning/removing presenters.
 */
public class SessionEditorController {
    
    private Stage stage;
    private Session session;
    private EventDetailController parentController;
    private PresenterDAO presenterDAO;
    
    private TextField titleField;
    private TextArea descriptionArea;
    private DatePicker startDatePicker;
    private Spinner<Integer> startHourSpinner;
    private Spinner<Integer> startMinSpinner;
    private Spinner<Integer> durationHoursSpinner;
    private TextField venueField;
    private Spinner<Integer> capacitySpinner;
    private ListView<String> assignedPresentersListView;
    private ComboBox<Presenter> presenterCombo;
    
    public SessionEditorController(Session session, EventDetailController parentController) {
        this.parentController = parentController;
        this.presenterDAO = DaoProvider.getPresenterDAO();
    }
    
    public void show() {
        stage = new Stage();
        stage.setTitle("Edit Session - " + session.getTitle());
        stage.setScene(createScene());
        stage.setWidth(700);
        stage.setHeight(800);
        stage.show();
    }
    
    private Scene createScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-color: white;");
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        
        root.getChildren().addAll(
            createBasicInfoSection(),
            createScheduleSection(),
            createVenueSection(),
            createPresenterSection(),
            createButtonSection()
        );
        
        return new Scene(scrollPane);
    }
    
    private VBox createBasicInfoSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Basic Information");
        title.setFont(new Font("System Bold", 16));
        
        Label titleLabel = new Label("Session Title:");
        titleLabel.setStyle("-fx-font-weight: bold;");
        titleField = new TextField(session.getTitle());
        titleField.setPrefWidth(500);
        
        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-weight: bold;");
        descriptionArea = new TextArea(session.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(80);
        
        section.getChildren().addAll(title, titleLabel, titleField, descLabel, descriptionArea);
        return section;
    }
    
    private VBox createScheduleSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Schedule");
        title.setFont(new Font("System Bold", 16));
        
        HBox startBox = new HBox(10);
        Label startLabel = new Label("Start Date & Time:");
        startLabel.setStyle("-fx-font-weight: bold;");
        startLabel.setPrefWidth(150);
        
        startDatePicker = new DatePicker(session.getStartTime().toLocalDate());
        startHourSpinner = new Spinner<>(0, 23, session.getStartTime().getHour());
        startMinSpinner = new Spinner<>(0, 59, session.getStartTime().getMinute());
        
        startBox.getChildren().addAll(startLabel, startDatePicker, 
            new Label("Hour:"), startHourSpinner,
            new Label("Min:"), startMinSpinner);
        
        HBox durationBox = new HBox(10);
        Label durationLabel = new Label("Duration (hours):");
        durationLabel.setStyle("-fx-font-weight: bold;");
        durationLabel.setPrefWidth(150);
        
        long durationHours = java.time.temporal.ChronoUnit.HOURS.between(
            session.getStartTime(), session.getEndTime());
        durationHoursSpinner = new Spinner<>(1, 24, (int) durationHours);
        
        durationBox.getChildren().addAll(durationLabel, durationHoursSpinner);
        
        section.getChildren().addAll(title, startBox, durationBox);
        return section;
    }
    
    private VBox createVenueSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Venue & Capacity");
        title.setFont(new Font("System Bold", 16));
        
        Label venueLabel = new Label("Venue:");
        venueLabel.setStyle("-fx-font-weight: bold;");
        venueField = new TextField(session.getVenue());
        venueField.setPrefWidth(300);
        
        Label capacityLabel = new Label("Capacity:");
        capacityLabel.setStyle("-fx-font-weight: bold;");
        capacitySpinner = new Spinner<>(1, 5000, session.getCapacity());
        
        HBox venueBox = new HBox(10, venueLabel, venueField);
        HBox capacityBox = new HBox(10, capacityLabel, capacitySpinner);
        
        section.getChildren().addAll(title, venueBox, capacityBox);
        return section;
    }
    
    private VBox createPresenterSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label title = new Label("Presenters");
        title.setFont(new Font("System Bold", 16));
        
        // Assigned presenters list
        Label assignedLabel = new Label("Assigned Presenters:");
        assignedLabel.setStyle("-fx-font-weight: bold;");
        
        List<String> presentersDisplay = session.getPresenterIds().stream()
                .map(id -> {
                    try {
                        return presenterDAO.findById(id)
                                .map(p -> p.getFullName() + " (" + p.getPresenterRole() + ")")
                                .orElse("Unknown Presenter #" + id);
                    } catch (Exception e) {
                        return "Unknown Presenter #" + id;
                    }
                })
                .collect(Collectors.toList());
        
        assignedPresentersListView = new ListView<>(FXCollections.observableArrayList(presentersDisplay));
        assignedPresentersListView.setPrefHeight(150);
        assignedPresentersListView.setStyle("-fx-border-color: #bbb; -fx-border-radius: 3;");
        
        // Remove selected presenter button
        Button removeBtn = createButton("Remove Selected", "#e74c3c");
        removeBtn.setOnAction(e -> handleRemovePresenter());
        
        // Add presenters combo
        Label addLabel = new Label("Add Presenter:");
        addLabel.setStyle("-fx-font-weight: bold;");
        
        List<Presenter> allPresenters = presenterDAO.findAll();
        presenterCombo = new ComboBox<>(FXCollections.observableArrayList(allPresenters));
        presenterCombo.setCellFactory(lv -> new ListCell<Presenter>() {
            @Override
            protected void updateItem(Presenter p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty ? "" : p.getFullName() + " (" + p.getPresenterRole() + ")");
            }
        });
        presenterCombo.setButtonCell(new ListCell<Presenter>() {
            @Override
            protected void updateItem(Presenter p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty ? "Select presenter..." : p.getFullName() + " (" + p.getPresenterRole() + ")");
            }
        });
        presenterCombo.setPrefWidth(300);
        
        Button addBtn = createButton("Add Presenter", "#27ae60");
        addBtn.setOnAction(e -> handleAddPresenter());
        
        HBox addBox = new HBox(10, presenterCombo, addBtn);
        
        section.getChildren().addAll(title, assignedLabel, assignedPresentersListView, removeBtn, 
            new Separator(), addLabel, addBox);
        return section;
    }
    
    private VBox createButtonSection() {
        VBox section = new VBox(10);
        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-alignment: center-right;");
        
        Button saveBtn = createButton("Save", "#3498db");
        saveBtn.setOnAction(e -> handleSave());
        
        Button cancelBtn = createButton("Cancel", "#95a5a6");
        cancelBtn.setOnAction(e -> stage.close());
        
        buttons.getChildren().addAll(saveBtn, cancelBtn);
        section.getChildren().add(buttons);
        return section;
    }
    
    private void handleAddPresenter() {
        Presenter selected = presenterCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a Presenter", "Please choose a presenter from the list.");
            return;
        }
        
        int presenterId = selected.getId();
        if (!session.getPresenterIds().contains(presenterId)) {
            session.addPresenter(presenterId);
            refreshPresentersList();
            presenterCombo.getSelectionModel().clearSelection();
        } else {
            showInfo("Already Assigned", "This presenter is already assigned to the session.");
        }
    }
    
    private void handleRemovePresenter() {
        int selectedIdx = assignedPresentersListView.getSelectionModel().getSelectedIndex();
        if (selectedIdx < 0) {
            showError("Select a Presenter", "Please select a presenter to remove.");
            return;
        }
        
        int presenterId = session.getPresenterIds().get(selectedIdx);
        session.removePresenter(presenterId);
        refreshPresentersList();
    }
    
    private void refreshPresentersList() {
        List<String> presentersDisplay = session.getPresenterIds().stream()
                .map(id -> {
                    try {
                        return presenterDAO.findById(id)
                                .map(p -> p.getFullName() + " (" + p.getPresenterRole() + ")")
                                .orElse("Unknown Presenter #" + id);
                    } catch (Exception e) {
                        return "Unknown Presenter #" + id;
                    }
                })
                .collect(Collectors.toList());
        
        assignedPresentersListView.setItems(FXCollections.observableArrayList(presentersDisplay));
    }
    
    private void handleSave() {
        try {
            // Update session details
            session.setTitle(titleField.getText());
            session.setDescription(descriptionArea.getText());
            session.setVenue(venueField.getText());
            session.setCapacity(capacitySpinner.getValue());
            
            // Update times
            java.time.LocalDateTime newStart = startDatePicker.getValue()
                    .atTime(startHourSpinner.getValue(), startMinSpinner.getValue());
            java.time.LocalDateTime newEnd = newStart.plusHours(durationHoursSpinner.getValue());
            session.setStartTime(newStart);
            session.setEndTime(newEnd);
            
            stage.close();
            
            if (parentController != null) {
                parentController.refreshSessions();
            }
            
            showInfo("Success", "Session saved successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to save session: " + e.getMessage());
        }
    }
    
    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-padding: 8px 20px; -fx-font-weight: bold;");
        return btn;
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
