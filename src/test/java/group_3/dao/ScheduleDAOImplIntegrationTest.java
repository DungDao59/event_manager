package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import group_3.dao.impl.ScheduleDAOImpl;
import group_3.model.ScheduleEntry;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class ScheduleDAOImplIntegrationTest {

    private String h2Url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
    private MockedStatic<DatabaseConnection> dbStatic;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            DatabaseTestUtils.runClasspathSql(c, "sql/schema.sql");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u','p','U','ATTENDEE')");
            // create an event and session to satisfy FK when inserting schedule entries
            c.createStatement().execute("INSERT INTO event (name,type,start_date,end_date,location,duration,status) VALUES ('E','CONFERENCE','2026-01-01','2026-01-02','L',1,'SCHEDULED')");
            c.createStatement().execute("INSERT INTO session (event_id,title,description,start_time,end_time,venue,capacity) VALUES (1,'S','d','2026-01-01 10:00:00','2026-01-01 11:00:00','V',10)");
        }

        dbStatic = Mockito.mockStatic(DatabaseConnection.class);
        dbStatic.when(() -> DatabaseConnection.getConnection()).thenAnswer(invocation -> DriverManager.getConnection(h2Url, "sa", ""));
    }

    @AfterEach
    void tearDown() {
        if (dbStatic != null) dbStatic.close();
    }

    @Test
    void create_find_and_delete_schedule_entry() {
        ScheduleDAOImpl dao = new ScheduleDAOImpl();

        ScheduleEntry e = new ScheduleEntry(0, 1, 1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        dao.create(e);

        ArrayList<ScheduleEntry> list = dao.findAllScheduleByUserId(1);
        assertTrue(list.size() >= 1);

        // delete by user and session
        boolean deleted = dao.deleteByUserAndSession(1, 1);
        assertTrue(deleted);
    }
}