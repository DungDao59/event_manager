package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import group_3.dao.impl.AttendeeDAOImpl;
import group_3.model.Attendee;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class AttendeeDAOImplIntegrationTest {

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
    void create_find_update_and_delete_attendee() throws Exception {
        AttendeeDAOImpl dao = new AttendeeDAOImpl();

        // H2 doesn't support the Postgres-specific INSERT ... RETURNING or casting used in create(); insert rows directly then use DAO methods that read/update/delete
        int personId;
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('john_doe','pass','John','ATTENDEE')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='john_doe'");
            rs.next();
            personId = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", personId));
        }

        Optional<group_3.model.Attendee> fetched = dao.findById(personId);
        assertTrue(fetched.isPresent());
        assertEquals("john_doe", fetched.get().getUsername());

        var a = fetched.get();
        a.setFullName("John Doe Updated");
        a.setHistory("{\"h\":1}");
        dao.update(a);

        Optional<group_3.model.Attendee> updated = dao.findById(personId);
        assertTrue(updated.isPresent());
        assertEquals("John Doe Updated", updated.get().getFullName());

        dao.delete(personId);
        assertTrue(dao.findById(personId).isEmpty());
    }
}