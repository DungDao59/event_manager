package group_3.controller;

import group_3.model.Ticket;
import group_3.security.AuthContext;
import group_3.service.RegistrationService.RegistrationService;
import group_3.service.RegistrationService.RegistrationServiceImpl;
import group_3.util.QRCode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TicketsController {
    private final RegistrationService registrationService = new RegistrationServiceImpl();

    // Get the actual logged-in user ID from AuthContext
    private int getCurrentUserId() {
        Integer userId = AuthContext.getCurrentUserId();
        return userId != null ? userId : -1;
    }

    private Stage stage;
    private ListView<Ticket> ticketList;
    private ImageView qrImageView;
    private Label qrPlaceholder;

    public void show() {
        stage = new Stage();
        stage.setTitle("My Tickets & QR Codes");
        stage.setScene(createScene());
        stage.setWidth(800);
        stage.setHeight(500);
        stage.show();
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        //left: purchase tickets
        VBox leftBox = new VBox(10);
        Label listLabel = new Label("Your Purchased Tickets");
        listLabel.setFont(new Font("System Bold", 16));

        ticketList = new ListView<>();

        ticketList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Ticket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display Ticket
                    setText("Ticket #" + item.getTicketID() + " - " + item.getType());
                }
            }
        });

        // When a user clicks a ticket, generate the QR code
        ticketList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showQRCode(newVal);
            }
        });

        leftBox.getChildren().addAll(listLabel, ticketList);
        VBox.setVgrow(ticketList, Priority.ALWAYS);

        //Right: QR
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        Label qrTitle = new Label("Entry QR Code");
        qrTitle.setFont(new Font("System Bold", 14));

        qrImageView = new ImageView();
        qrImageView.setFitWidth(250);
        qrImageView.setFitHeight(250);
        qrImageView.setPreserveRatio(true);

        qrPlaceholder = new Label("Select a ticket on the left to see your QR code.");
        qrPlaceholder.setStyle("-fx-text-fill: #adb5bd; -fx-font-style: italic;");

        rightBox.getChildren().addAll(qrTitle, qrImageView, qrPlaceholder);


        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(leftBox, rightBox);
        splitPane.setDividerPositions(0.4);

        root.setCenter(splitPane);

        loadTickets();

        return new Scene(root);
    }

    private void loadTickets() {
        int userId = getCurrentUserId();
        if (userId == -1) {
            ticketList.getItems().clear();
            qrPlaceholder.setText("Please log in to view your tickets.");
            qrPlaceholder.setStyle("-fx-text-fill: red;");
            return;
        }
        ticketList.getItems().setAll(registrationService.getTicketsForAttendee(userId));
    }

    private void showQRCode(Ticket ticket) {
        String qrData = ticket.getQRpath(); // Get the unique string from DB

        if (qrData != null && !qrData.isEmpty()) {
            Image image = QRCode.generateQRImage(qrData, 300, 300);

            qrImageView.setImage(image);
            qrPlaceholder.setText("Scan this code at the venue entrance.");
            qrPlaceholder.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            qrImageView.setImage(null);
            qrPlaceholder.setText("Error: No QR data found for this ticket.");
            qrPlaceholder.setStyle("-fx-text-fill: red;");
        }
    }
}