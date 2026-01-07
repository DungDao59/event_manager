package group_3.ui;

import group_3.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Group 3
 *
 * Main JavaFX Application with Authentication.
 * Starts with Login screen and navigates based on user role.
 */
public class AuthApp extends Application {
    
    @Override
    public void init() throws Exception {
        // Initialize database before UI loads
        DatabaseConnection.setUpDatabase();
        System.out.println("Database initialized successfully");
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Login - Event Management System");
        primaryStage.setScene(scene);
        primaryStage.setWidth(550);
        primaryStage.setHeight(650);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(550);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
