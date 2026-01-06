package group_3;

import group_3.util.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        try {
            // Force reset database - this runs schema first, then initial data
            DatabaseConnection.forceResetDatabase();
        } catch (Exception e) {
            System.err.println("❌ Failed to setup database");
            e.printStackTrace();
        }
    }
}
