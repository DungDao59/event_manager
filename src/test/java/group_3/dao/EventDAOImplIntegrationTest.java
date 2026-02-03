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

import group_3.dao.impl.EventDAOImpl;
import group_3.model.Event;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class EventDAOImplIntegrationTest {

    private String h2Url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
    private MockedStatic<DatabaseConnection> dbStatic;

    @BeforeEach
    void setUp() throws Exception {
        // create db and run schema + initial data
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            DatabaseTestUtils.runClasspathSql(c, "sql/schema.sql");
            // Don't execute full initial_data.sql in tests (contains queries incompatible with H2)
            // DatabaseTestUtils.runClasspathSql(c, "sql/initial_data.sql");
        }

        // Mock DatabaseConnection.getConnection() to return fresh H2 connections
        dbStatic = Mockito.mockStatic(DatabaseConnection.class);
        dbStatic.when(() -> DatabaseConnection.getConnection()).thenAnswer(invocation -> DriverManager.getConnection(h2Url, "sa", ""));
    }

    @AfterEach
    void tearDown() {
        if (dbStatic != null) dbStatic.close();
    }

    @Test
    void create_and_find_by_id_and_count_and_exists() {
        EventDAOImpl dao = new EventDAOImpl();
        Event e = new Event(0, "Integration Event", EventType.CONFERENCE,
                LocalDateTime.of(2026, 1, 1, 9, 0), LocalDateTime.of(2026, 1, 1, 17, 0),
                "Test Hall", 1, EventStatus.SCHEDULED, null);

        dao.create(e);
        assertTrue(e.getEventId() > 0, "Created event should have generated id");

        Optional<Event> fetched = dao.findById(e.getEventId());
        assertTrue(fetched.isPresent());
        assertEquals("Integration Event", fetched.get().getName());

        int cnt = dao.count();
        assertTrue(cnt >= 1);

        assertTrue(dao.exists(e.getEventId()));

        // cleanup
        dao.delete(e.getEventId());
        assertFalse(dao.exists(e.getEventId()));
    }
}
