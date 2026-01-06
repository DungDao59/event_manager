package group_3.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL  = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");
    
    // Connection cache for better performance
    private static Connection cachedConnection = null;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        
        // Return cached connection if valid
        if (cachedConnection != null && !cachedConnection.isClosed()) {
            return cachedConnection;
        }
        
        cachedConnection = DriverManager.getConnection(URL, USER, PASS);
        return cachedConnection;
    }

    /**
     * Check if database tables already exist to avoid re-running schema
     */
    private static boolean isDatabaseInitialized() {
        String checkQuery = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'person')";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkQuery)) {
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            System.err.println("Could not check database state: " + e.getMessage());
        }
        return false;
    }

    private static void executeSQLScript(Connection conn, String scriptPath)
            throws SQLException {

        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(scriptPath);

        if (inputStream == null) {
            throw new SQLException("SQL script not found: " + scriptPath);
        }

        StringBuilder sql = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("--")) continue;

                sql.append(line).append(" ");
                if (line.endsWith(";")) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql.toString());
                    }
                    sql.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new SQLException("Failed to read SQL script: " + scriptPath, e);
        }
    }

    // ==================================================
    // PUBLIC ENTRY POINTS
    // ==================================================

    public static void setUpDatabase() {
        // Skip setup if database is already initialized
        if (isDatabaseInitialized()) {
            System.out.println("✅ Database already initialized, skipping setup");
            return;
        }
        
        try {
            setupSchema();
            loadInitialData();
        } catch (SQLException e) {
            System.err.println("[Error] Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Force reset the database - drops all tables and recreates them
     */
    public static void forceResetDatabase() {
        try {
            setupSchema();
            loadInitialData();
            System.out.println("✅ Database reset completed");
        } catch (SQLException e) {
            System.err.println("[Error] Database reset failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setupSchema() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            executeSQLScript(conn, "sql/schema.sql");
            System.out.println("✅ Schema setup completed");
        }
    }

    public static void loadInitialData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            executeSQLScript(conn, "sql/initial_data.sql");
            System.out.println("✅ Initial data loaded");
        }catch(SQLException e){
            System.err.println("[Error] Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
