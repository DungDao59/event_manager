package group_3.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import group_3.controller.PublicEventBrowserController;
import group_3.util.DatabaseConnection;

/**
 * Main JavaFX Application for Event Management System.
 * Launches the Public Event Browser as the default landing page.
 * 
 * Flow:
 * - Anonymous visitors can browse events without logging in
 * - Users can click "Sign In" or "Register" to access authenticated features
 * 
 * @author Group 3
 */
public class EventManagementApp extends Application {
    
    @Override
    public void init() throws Exception {
        // Initialize database before UI loads
        DatabaseConnection.setUpDatabase();
        System.out.println("Database initialized successfully");
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Launch Public Event Browser as default landing page
        // Anonymous visitors can browse events without logging in
        PublicEventBrowserController browserController = new PublicEventBrowserController();
        javafx.scene.Scene scene = browserController.getScene();
        
        primaryStage.setTitle("Event Management System");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
