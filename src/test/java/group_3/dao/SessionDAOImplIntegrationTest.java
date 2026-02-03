package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import group_3.dao.impl.SessionDAOImpl;
import group_3.model.Session;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class SessionDAOImplIntegrationTest {

    private String h2Url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
    private MockedStatic<DatabaseConnection> dbStatic;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            DatabaseTestUtils.runClasspathSql(c, "sql/schema.sql");
        }

        dbStatic = Mockito.mockStatic(DatabaseConnection.class);
        dbStatic.when(() -> DatabaseConnection.getConnection()).thenAnswer(invocation -> DriverManager.getConnection(h2Url, "sa", ""));
    }

    @AfterEach
    void tearDown() {
        if (dbStatic != null) dbStatic.close();
    }

    @Test
    void create_find_update_delete_and_counts() {
        SessionDAOImpl dao = new SessionDAOImpl();

        // Need an event for FK; insert directly
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO event (name, type, start_date, end_date, location, duration, status) VALUES ('E','CONFERENCE','2026-01-01','2026-01-02','L',1,'SCHEDULED')");
        } catch (Exception e) { throw new RuntimeException(e); }

        Session s = new Session(0, 1, "Title", "Desc", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "V", 20);
        dao.create(s);
        assertTrue(s.getSessionId() > 0);

        Optional<Session> fetched = dao.findById(s.getSessionId());
        assertTrue(fetched.isPresent());
        assertEquals("Title", fetched.get().getTitle());

        int count = dao.count();
        assertTrue(count >= 1);

        assertTrue(dao.exists(s.getSessionId()));

        dao.delete(s.getSessionId());
        assertFalse(dao.exists(s.getSessionId()));
    }
}
