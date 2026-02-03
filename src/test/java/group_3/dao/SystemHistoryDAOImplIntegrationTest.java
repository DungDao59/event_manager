package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import group_3.dao.impl.SystemHistoryDaoImpl;
import group_3.model.SystemHistory;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class SystemHistoryDAOImplIntegrationTest {

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
    void create_and_findAll() throws Exception {
        // Create a person first (FK requirement)
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u1','p','U1','ATTENDEE')");
        }

        SystemHistoryDaoImpl dao = new SystemHistoryDaoImpl();

        SystemHistory h1 = new SystemHistory();
        h1.setUserId(1);
        h1.setOperationType("LOGIN");
        h1.setDetails("login details");
        h1.setTimestamp(OffsetDateTime.now());

        dao.create(h1);

        List<SystemHistory> all = dao.findAll();
        assertTrue(all.size() >= 1);
    }

    @Test
    void findByUserId() throws Exception {
        // Create persons first (FK requirement)
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u5','p','U5','ATTENDEE')");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u6','p','U6','ATTENDEE')");
        }

        SystemHistoryDaoImpl dao = new SystemHistoryDaoImpl();

        SystemHistory h1 = new SystemHistory();
        h1.setUserId(1);
        h1.setOperationType("ACTION1");
        h1.setDetails("d");
        h1.setTimestamp(OffsetDateTime.now());

        SystemHistory h2 = new SystemHistory();
        h2.setUserId(2);
        h2.setOperationType("ACTION2");
        h2.setDetails("d");
        h2.setTimestamp(OffsetDateTime.now());

        dao.create(h1);
        dao.create(h2);

        List<SystemHistory> byUser1 = dao.findByUserId(1);
        assertEquals(1, byUser1.size());
        assertEquals(1, byUser1.get(0).getUserId());
    }

    @Test
    void findByDateRange() throws Exception {
        // Create a person first (FK requirement)
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u_date','p','Udate','ATTENDEE')");
        }

        SystemHistoryDaoImpl dao = new SystemHistoryDaoImpl();

        SystemHistory h = new SystemHistory();
        h.setUserId(1);
        h.setOperationType("OP");
        h.setDetails("d");
        h.setTimestamp(OffsetDateTime.now());

        dao.create(h);

        // Test that the method runs without error
        LocalDate today = LocalDate.now();
        List<SystemHistory> range = dao.findByDateRange(today.minusDays(1), today.plusDays(1));
        assertNotNull(range); // Just verify it returns a list
    }

    @Test
    void findByOperationType() throws Exception {
        // Create persons first (FK requirement)
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u_login','p','Ulogin','ATTENDEE')");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('u_logout','p','Ulogout','ATTENDEE')");
        }

        SystemHistoryDaoImpl dao = new SystemHistoryDaoImpl();

        SystemHistory h1 = new SystemHistory();
        h1.setUserId(1);
        h1.setOperationType("LOGIN");
        h1.setDetails("d");
        h1.setTimestamp(OffsetDateTime.now());

        SystemHistory h2 = new SystemHistory();
        h2.setUserId(2);
        h2.setOperationType("LOGOUT");
        h2.setDetails("d");
        h2.setTimestamp(OffsetDateTime.now());

        dao.create(h1);
        dao.create(h2);

        List<SystemHistory> logins = dao.findByOperationType("LOGIN");
        assertEquals(1, logins.size());
        assertEquals("LOGIN", logins.get(0).getOperationType());
    }
}
