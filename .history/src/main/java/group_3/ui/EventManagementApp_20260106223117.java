package group_3.ui;

import group_3.controller.EventListController;
import group_3.controller.UserBookingController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX Application for Event Management System.
 * Launches the Event Management UI (for Event Admins).
 * 
 * @author Group 3
 */
public class EventManagementApp extends Application {
    @Override
    public void start(Stage stage) {
        new UserBookingController().show();
    }

    public static void main(String[] args) {
        launch(args);
    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        EventListController listController = new EventListController();
//        javafx.scene.Scene scene = listController.getScene();
//
//        primaryStage.setTitle("Event Management System");
//        primaryStage.setScene(scene);
//        primaryStage.setWidth(1200);
//        primaryStage.setHeight(800);
//        primaryStage.setMinWidth(1000);
//        primaryStage.setMinHeight(600);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
}
