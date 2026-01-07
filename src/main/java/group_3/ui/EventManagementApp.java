package group_3.ui;

import group_3.controller.EventListController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Group 3
 *
 * Main JavaFX Application for Event Management System.
 * Launches the Event Management UI (for Event Admins).
 */
public class EventManagementApp extends Application {
    @Override
    public void start(Stage stage) {
        EventListController controller = new EventListController();
        stage.setScene(controller.getScene());
        stage.setTitle("Event Management System");
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
