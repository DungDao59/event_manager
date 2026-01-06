package group_3.ui;

import group_3.controller.EventListController;
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
