package group_3.controller;

import java.io.File;
import java.time.LocalDateTime;

import group_3.model.Event;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.service.EventAdminService.EventAdminService;
import group_3.service.EventAdminService.EventAdminServiceImpl;
import group_3.util.DaoProvider;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Group 3
 * Controller for creating and editing events, handling event form input
 * and validation in the JavaFX application.
 */

public class EventFormController {
    
    private Stage stage;
    private Event eventToEdit;
    private EventListController listController;
    private EventAdminService eventAdminService;
    
    private TextField idField;
    private TextField nameField;
    private ComboBox<EventType> typeCombo;
    private TextField locationField;
    private Label imagePathLabel;
    private String selectedImagePath = null;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<Integer> startHourCombo;
    private ComboBox<Integer> startMinuteCombo;
    private ComboBox<Integer> endHourCombo;
    private ComboBox<Integer> endMinuteCombo;
    private Spinner<Integer> durationSpinner;
    private ComboBox<EventStatus> statusCombo;
    private ListView<String> sessionListView;
    private TextField newSessionField;
    private Label errorLabel;
    
    public EventFormController(Event event, EventListController listController) {
        this.eventToEdit = event;
        this.listController = listController;
        this.eventAdminService = new EventAdminServiceImpl();
    }
    
    public void show() {
        stage = new Stage();
        stage.setTitle(eventToEdit == null ? "Create Event" : "Edit Event");
        stage.setScene(createScene());
        stage.setWidth(700);
        stage.setHeight(800);
        stage.show();
    }
    
    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(createFormContent());
        root.setCenter(scrollPane);
        
        root.setBottom(createButtonBar());
        
        return new Scene(root);
    }
    
    private VBox createFormContent() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");
        
        // Event ID
        form.getChildren().add(createFormField("Event ID", idField = new TextField(), true));
        idField.setEditable(false);
        
        // Event Name
        form.getChildren().add(createFormField("Event Name *", nameField = new TextField(), false));
        
        // Event Type
        HBox typeBox = new HBox(10);
        Label typeLabel = new Label("Event Type *");
        typeLabel.setPrefWidth(120);
        typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList(EventType.values()));
        typeCombo.setPrefWidth(500);
        typeBox.getChildren().addAll(typeLabel, typeCombo);
        form.getChildren().add(typeBox);
        
        // Location
        form.getChildren().add(createFormField("Location *", locationField = new TextField(), false));
        
        // Image Upload Section
        Label imageLabel = new Label("Event Image");
        imageLabel.setFont(new Font("System Bold", 14));
        form.getChildren().add(imageLabel);
        
        VBox imageUploadBox = createImageUploadSection();
        form.getChildren().add(imageUploadBox);
        
        // Date and Time Section
        Label dateTimeLabel = new Label("Start Date & Time *");
        dateTimeLabel.setFont(new Font("System Bold", 14));
        form.getChildren().add(dateTimeLabel);

        HBox dateTimeBox = new HBox(15);
        startDatePicker = new DatePicker();
        startDatePicker.setPrefWidth(150);
        startDatePicker.setEditable(false); // Prevent manual typing
        
        startHourCombo = new ComboBox<>();
        for (int i = 0; i < 24; i++) startHourCombo.getItems().add(i);
        startHourCombo.setPrefWidth(80);
        
        startMinuteCombo = new ComboBox<>();
        for (int i = 0; i < 60; i += 15) startMinuteCombo.getItems().add(i);
        startMinuteCombo.setPrefWidth(80);
        
        dateTimeBox.getChildren().addAll(
            new Label("Date:"), startDatePicker,
            new Label("Hour:"), startHourCombo,
            new Label("Minute:"), startMinuteCombo
        );
        form.getChildren().add(dateTimeBox);
        
        // End Date & Time
        Label endLabel = new Label("End Date & Time *");
        endLabel.setFont(new Font("System Bold", 14));
        form.getChildren().add(endLabel);
        
        HBox endDateTimeBox = new HBox(15);
        endDatePicker = new DatePicker();
        endDatePicker.setPrefWidth(150);
        endDatePicker.setEditable(false); // Prevent manual typing
        
        endHourCombo = new ComboBox<>();
        for (int i = 0; i < 24; i++) endHourCombo.getItems().add(i);
        endHourCombo.setPrefWidth(80);
        
        endMinuteCombo = new ComboBox<>();
        for (int i = 0; i < 60; i += 15) endMinuteCombo.getItems().add(i);
        endMinuteCombo.setPrefWidth(80);
        
        endDateTimeBox.getChildren().addAll(
            new Label("Date:"), endDatePicker,
            new Label("Hour:"), endHourCombo,
            new Label("Minute:"), endMinuteCombo
        );
        form.getChildren().add(endDateTimeBox);
        
        // Duration
        HBox durationBox = new HBox(10);
        Label durationLabel = new Label("Duration (days) *");
        durationLabel.setPrefWidth(120);
        durationSpinner = new Spinner<>(1, 365, 1);
        durationSpinner.setPrefWidth(100);
        durationBox.getChildren().addAll(durationLabel, durationSpinner);
        form.getChildren().add(durationBox);
        
        // Status
        HBox statusBox = new HBox(10);
        Label statusLabel = new Label("Status *");
        statusLabel.setPrefWidth(120);
        statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(EventStatus.values()));
        statusCombo.setPrefWidth(500);
        statusBox.getChildren().addAll(statusLabel, statusCombo);
        form.getChildren().add(statusBox);
        
        // Sessions
        Label sessionLabel = new Label("Associated Sessions");
        sessionLabel.setFont(new Font("System Bold", 14));
        form.getChildren().add(sessionLabel);
        
        sessionListView = new ListView<>();
        sessionListView.setPrefHeight(100);
        form.getChildren().add(sessionListView);
        
        // Session input and management removed - sessions are read-only in edit view
        Button removeSessionBtn = new Button("Remove Selected");
        removeSessionBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 15px;");
        removeSessionBtn.setOnAction(e -> handleRemoveSession());
        form.getChildren().add(removeSessionBtn);
        
        // Error Label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        form.getChildren().add(errorLabel);
        
        // Populate if editing
        if (eventToEdit != null) {
            populateForm();
        }
        
        return form;
    }
    
    private HBox createFormField(String label, TextField field, boolean isReadOnly) {
        HBox box = new HBox(10);
        Label lbl = new Label(label);
        lbl.setPrefWidth(120);
        field.setPrefWidth(500);
        if (isReadOnly) field.setDisable(true);
        box.getChildren().addAll(lbl, field);
        return box;
    }
    
    private VBox createImageUploadSection() {
        VBox section = new VBox(10);
        
        // Drop zone
        VBox dropZone = new VBox(20);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.setPrefHeight(150);
        dropZone.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-style: dashed; " +
                         "-fx-background-color: #ecf0f1; -fx-border-radius: 5;");
        
        Label dropLabel = new Label("Drag & Drop Image Here\nor");
        dropLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        Button browseBtn = new Button("Browse Files");
        browseBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8px 20px;");
        browseBtn.setOnAction(e -> handleBrowseImage());
        
        imagePathLabel = new Label("No image selected");
        imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
        
        dropZone.getChildren().addAll(dropLabel, browseBtn, imagePathLabel);
        
        // Enable drag and drop
        dropZone.setOnDragOver(this::handleDragOver);
        dropZone.setOnDragDropped(this::handleDragDropped);
        
        Button clearBtn = new Button("Clear Image");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5px 15px;");
        clearBtn.setOnAction(e -> handleClearImage());
        
        section.getChildren().addAll(dropZone, clearBtn);
        return section;
    }
    
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Event Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            imagePathLabel.setText("Selected: " + selectedFile.getName());
            imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }
    
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }
    
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        
        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            String fileName = file.getName().toLowerCase();
            
            // Check if it's an image file
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || 
                fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || 
                fileName.endsWith(".bmp")) {
                
                selectedImagePath = file.getAbsolutePath();
                imagePathLabel.setText("Selected: " + file.getName());
                imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
                success = true;
            } else {
                imagePathLabel.setText("Invalid file type. Please select an image.");
                imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
        
        event.setDropCompleted(success);
        event.consume();
    }
    
    private void handleClearImage() {
        selectedImagePath = null;
        imagePathLabel.setText("No image selected");
        imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
    }
    
    private HBox createButtonBar() {
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(20));
        buttonBar.setStyle("-fx-background-color: #ecf0f1;");
        buttonBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button saveBtn = new Button("Save Event");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 30px; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> handleSave());
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 30px;");
        cancelBtn.setOnAction(e -> stage.close());
        
        buttonBar.getChildren().addAll(saveBtn, cancelBtn);
        return buttonBar;
    }
    
    private void populateForm() {
        idField.setText(String.valueOf(eventToEdit.getEventId()));
        nameField.setText(eventToEdit.getName());
        typeCombo.setValue(eventToEdit.getType());
        locationField.setText(eventToEdit.getLocation());
        
        if (eventToEdit.getEventImage() != null && !eventToEdit.getEventImage().isEmpty()) {
            selectedImagePath = eventToEdit.getEventImage();
            File imageFile = new File(selectedImagePath);
            if (imageFile.exists()) {
                imagePathLabel.setText("Selected: " + imageFile.getName());
                imagePathLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
        }
        
        durationSpinner.getValueFactory().setValue(eventToEdit.getDuration());
        statusCombo.setValue(eventToEdit.getStatus());
        
        if (eventToEdit.getStartDate() != null) {
            startDatePicker.setValue(eventToEdit.getStartDate().toLocalDate());
            startHourCombo.setValue(eventToEdit.getStartDate().getHour());
            startMinuteCombo.setValue(eventToEdit.getStartDate().getMinute());
        }
        
        if (eventToEdit.getEndDate() != null) {
            endDatePicker.setValue(eventToEdit.getEndDate().toLocalDate());
            endHourCombo.setValue(eventToEdit.getEndDate().getHour());
            endMinuteCombo.setValue(eventToEdit.getEndDate().getMinute());
        }
        
        // Populate sessions with names instead of just IDs
        group_3.dao.SessionDAO sessionDAO = DaoProvider.getSessionDAO();
        java.util.List<String> sessionDisplayList = eventToEdit.getSessionIds().stream()
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
        
        sessionListView.setItems(FXCollections.observableArrayList(sessionDisplayList));
    }
    
    private void handleAddSession() {
        String sessionId = newSessionField.getText().trim();
        if (!sessionId.isEmpty()) {
            sessionListView.getItems().add(sessionId);
            newSessionField.clear();
        }
    }
    
    private void handleRemoveSession() {
        int index = sessionListView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            sessionListView.getItems().remove(index);
        }
    }
    
    private void handleSave() {
        if (!validateForm()) return;
        
        try {
            Event event = buildEvent();
            
            if (eventToEdit == null) {
                // Create new event using EventAdminService
                eventAdminService.createEvent(event);
                showSuccess("Event Created", "Event '" + event.getName() + "' has been created successfully.");
            } else {
                // Update existing event using EventAdminService
                eventAdminService.updateEvent(event);
                showSuccess("Event Updated", "Event '" + event.getName() + "' has been updated successfully.");
            }
            
            // Refresh the list view to show the changes
            if (listController != null) {
                listController.refreshEvents();
            }
            stage.close();
        } catch (Exception e) {
            showError("Error saving event", e.getMessage());
        }
    }
    
    private boolean validateForm() {
        errorLabel.setText("");
        
        if (nameField.getText().trim().isEmpty()) {
            errorLabel.setText("Event Name is required");
            return false;
        }
        
        if (typeCombo.getValue() == null) {
            errorLabel.setText("Event Type is required");
            return false;
        }
        
        if (locationField.getText().trim().isEmpty()) {
            errorLabel.setText("Location is required");
            return false;
        }
        
        if (startDatePicker.getValue() == null) {
            errorLabel.setText("Start Date is required");
            return false;
        }
        
        if (endDatePicker.getValue() == null) {
            errorLabel.setText("End Date is required");
            return false;
        }
        
        if (startHourCombo.getValue() == null || startMinuteCombo.getValue() == null) {
            errorLabel.setText("Start time is required");
            return false;
        }
        
        if (endHourCombo.getValue() == null || endMinuteCombo.getValue() == null) {
            errorLabel.setText("End time is required");
            return false;
        }
        
        if (statusCombo.getValue() == null) {
            errorLabel.setText("Status is required");
            return false;
        }
        
        return true;
    }
    
    private Event buildEvent() {
        LocalDateTime startDateTime = LocalDateTime.of(
            startDatePicker.getValue(),
            java.time.LocalTime.of(startHourCombo.getValue(), startMinuteCombo.getValue())
        );
        
        LocalDateTime endDateTime = LocalDateTime.of(
            endDatePicker.getValue(),
            java.time.LocalTime.of(endHourCombo.getValue(), endMinuteCombo.getValue())
        );
        
        // Parse eventId from field or use 0 for new events
        int eventId = 0;
        if (eventToEdit != null) {
            eventId = eventToEdit.getEventId();
        }
        
        Event event = new Event(
            eventId,
            nameField.getText().trim(),
            typeCombo.getValue(),
            startDateTime,
            endDateTime,
            locationField.getText().trim(),
            durationSpinner.getValue(),
            statusCombo.getValue(),
            selectedImagePath
        );
        
        for (String sessionId : sessionListView.getItems()) {
            event.addSession(sessionId);
        }
        
        return event;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Non-blocking
    }
}
