package group_3.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility to load mock data SQL script into database.
 */
public class LoadMockData {
    
    public static void main(String[] args) {
        String sqlFilePath = "src/main/resources/sql/mock_data.sql";
        
        try (Connection conn = DatabaseConnection.getConnection();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line).append(" ");
                
                // Execute when we hit a semicolon
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("✓ Executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("✗ Error executing: " + sql);
                        System.err.println("  " + e.getMessage());
                    }
                    sqlBuilder.setLength(0);
                }
            }
            
            System.out.println("\n✅ Mock data loaded successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to load mock data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
