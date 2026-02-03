package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Attendee;
import group_3.model.Presenter;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class PersonDAOImplIntegrationTest {

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
    void create_and_findById() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('user1','hash','User One','ATTENDEE')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='user1'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", id));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        Optional<group_3.model.Person> found = dao.findById(1);
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());
    }

    @Test
    void findByUsername_finds_attendee() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('att_user','pass','Attendee User','ATTENDEE')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='att_user'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", id));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        Optional<group_3.model.Person> found = dao.findByUsername("att_user");
        assertTrue(found.isPresent());
        assertTrue(found.get() instanceof Attendee);
    }

    @Test
    void findByUsername_finds_presenter() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('pres_user','pass','Presenter User','PRESENTER')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='pres_user'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'ROLE', '{}')", id));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        Optional<group_3.model.Person> found = dao.findByUsername("pres_user");
        assertTrue(found.isPresent());
        assertTrue(found.get() instanceof Presenter);
    }

    @Test
    void findAll_returns_mixed_roles() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('a1','p','A1','ATTENDEE')");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('p1','p','P1','PRESENTER')");
            var rs1 = c.createStatement().executeQuery("SELECT id FROM person WHERE username='a1'");
            rs1.next();
            int id1 = rs1.getInt(1);
            var rs2 = c.createStatement().executeQuery("SELECT id FROM person WHERE username='p1'");
            rs2.next();
            int id2 = rs2.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", id1));
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'R', '{}')", id2));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        List<group_3.model.Person> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void update_modifies_person() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('user2','pass','User Two','ATTENDEE')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='user2'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", id));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        Optional<group_3.model.Person> found = dao.findById(1);
        assertTrue(found.isPresent());
        found.get().setFullName("Updated Name");
        dao.update(found.get());

        Optional<group_3.model.Person> after = dao.findById(1);
        assertTrue(after.isPresent());
        assertEquals("Updated Name", after.get().getFullName());
    }

    @Test
    void delete_removes_person() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('del_user','p','Del','ATTENDEE')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='del_user'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO attendee (person_id, history) VALUES (%d, '{}')", id));
        }

        PersonDAOImpl dao = new PersonDAOImpl();
        Optional<group_3.model.Person> before = dao.findById(1);
        assertTrue(before.isPresent());

        dao.delete(1);

        Optional<group_3.model.Person> after = dao.findById(1);
        assertTrue(after.isEmpty());
    }
}
