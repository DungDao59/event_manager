package group_3.util;

/*
 * @author group_3
 */

import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");

    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e){
            System.err.println("[Failed] PostgresSQL JDBC Driver not found");
            throw new SQLException("[Failed] PostgresSQL driver missing", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void executeSQLScript(Connection conn, String scriptPath) throws SQLException{
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(scriptPath);
        if(inputStream == null){
            System.err.println("SQL script not found: " + scriptPath);
        }

        StringBuilder sql = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )){
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
        }catch(IOException e){
            throw new SQLException("[Failed] Failed to read SQL script: " + scriptPath, e);
        }

    }

    public static void setUpDatabase(){
        try(Connection conn = getConnection()){
            executeSQLScript(conn, "sql/schema.sql");
            System.out.println("[Success] Database schema setup complete");

//            executeSQLScript(conn,"sql/initial_data.sql");
//            System.out.println("[Success] Initial data loaded");
        }catch(SQLException e){
            System.err.println("[Error] Database setup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
