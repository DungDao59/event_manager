package group_3.util;

/**
 * Utility to force reset the database.
 * Run this to drop all tables and recreate them with initial data.
 */
public class DatabaseReset {
    public static void main(String[] args) {
        System.out.println("🔄 Forcing database reset...");
        DatabaseConnection.forceResetDatabase();
        System.out.println("✅ Database reset complete!");
    }
}
