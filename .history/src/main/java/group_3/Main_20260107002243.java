package group_3;

import group_3.util.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseConnection.setUpDatabase();
        } catch (Exception e) {
            System.err.println("❌ Failed to set up database");
            e.printStackTrace();
        }
    }
}
