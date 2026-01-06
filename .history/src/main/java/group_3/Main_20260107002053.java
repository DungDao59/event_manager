package group_3;

import group_3.util.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseConnection.loadInitialData();
        } catch (Exception e) {
            System.err.println("❌ Failed to seed data");
            e.printStackTrace();
        }
    }
}
