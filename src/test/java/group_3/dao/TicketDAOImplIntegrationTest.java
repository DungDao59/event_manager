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

import group_3.dao.impl.TicketDAOImpl;
import group_3.model.Ticket;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class TicketDAOImplIntegrationTest {

    private String h2Url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
    private MockedStatic<DatabaseConnection> dbStatic;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            DatabaseTestUtils.runClasspathSql(c, "sql/schema.sql");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u','p','U','ATTENDEE')");
            c.createStatement().execute("INSERT INTO attendee (person_id, history) VALUES (1, '{}')");
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
    void create_find_update_delete_and_queries() {
        TicketDAOImpl dao = new TicketDAOImpl();
        Ticket t = new Ticket();
        t.setAttendeeID(1);
        t.setEventID(1);
        t.setSessionID(1);
        t.setType(TicketType.GENERAL);
        t.setPrice(9.99);
        t.setStatus(TicketStatus.ACTIVE);
        t.setQRpath("qrcode");

        int id = dao.create(t);
        assertTrue(id > 0);

        Ticket found = dao.findById(id);
        assertNotNull(found);
        assertEquals(id, found.getTicketID());

        ArrayList<Ticket> byAtt = dao.findTicketByAttendeeId(1);
        assertTrue(byAtt.size() >= 1);

        ArrayList<Ticket> bySess = dao.findTicketBySessionId(1);
        assertTrue(bySess.size() >= 1);

        t.setTicketID(id);
        t.setPrice(19.99);
        dao.update(t);

        Ticket updated = dao.findById(id);
        assertEquals(19.99, updated.getPrice());

        assertTrue(dao.delete(id));
        assertNull(dao.findById(id));
    }
}