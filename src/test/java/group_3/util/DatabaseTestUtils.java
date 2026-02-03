package group_3.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Small test helper to create an H2 in-memory connection and run simple SQL scripts.
 */
public final class DatabaseTestUtils {
    private DatabaseTestUtils() {}

    public static Connection createH2Connection() throws SQLException {
        String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
        return DriverManager.getConnection(url, "sa", "");
    }

    public static void runClasspathSql(Connection conn, String resourcePath) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);

        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;

                sql.append(line).append("\n");
                if (line.endsWith(";")) {
                    String sqlStmt = sql.toString();
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sqlStmt);
                    }
                    sql.setLength(0);
                }
            }
            // execute leftover if any
            if (sql.length() > 0) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql.toString());
                }
            }
        }
    }
}
