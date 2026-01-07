package group_3;

import group_3.util.DatabaseConnection;

import javax.xml.crypto.Data;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseConnection.setupSchema();
            DatabaseConnection.loadInitialData();
        } catch (Exception e) {
            System.err.println("❌ Failed to seed data");
            e.printStackTrace();
        }
    }
}
