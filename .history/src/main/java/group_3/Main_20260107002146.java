package group_3;

import group_3.util.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        // Set up database (schema + initial data) - skips if already initialized
        DatabaseConnection.setUpDatabase();
    }
}
