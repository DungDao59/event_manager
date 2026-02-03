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

import group_3.dao.impl.PresenterDAOImpl;
import group_3.model.Presenter;
import group_3.util.DatabaseConnection;
import group_3.util.DatabaseTestUtils;

class PresenterDAOImplIntegrationTest {

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
    void create_find_and_update_presenter() throws Exception {
        PresenterDAOImpl dao = new PresenterDAOImpl();

        // Insert presenter directly due to H2 incompatibility with create() RETURNING
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('pres1','pass','Presenter One','PRESENTER')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='pres1'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'MASTER', '{}')", id));
        }

        Optional<Presenter> found = dao.findByUsername("pres1");
        assertTrue(found.isPresent());
        assertEquals("pres1", found.get().getUsername());
        assertEquals("MASTER", found.get().getPresenterRole());

        Presenter p = found.get();
        p.setPresenterRole("EXPERT");
        dao.update(p);

        Optional<Presenter> updated = dao.findByUsername("pres1");
        assertTrue(updated.isPresent());
        assertEquals("EXPERT", updated.get().getPresenterRole());
    }

    @Test
    void findAll_returnsAllPresenters() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('p1','p','P1','PRESENTER')");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('p2','p','P2','PRESENTER')");
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('att1','p','A1','ATTENDEE')");
            var rs1 = c.createStatement().executeQuery("SELECT id FROM person WHERE username='p1'");
            rs1.next();
            int id1 = rs1.getInt(1);
            var rs2 = c.createStatement().executeQuery("SELECT id FROM person WHERE username='p2'");
            rs2.next();
            int id2 = rs2.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'ROLE1', '{}')", id1));
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'ROLE2', '{}')", id2));
        }

        PresenterDAOImpl dao = new PresenterDAOImpl();
        List<Presenter> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void delete_removesPresenter() throws Exception {
        try (Connection c = DriverManager.getConnection(h2Url, "sa", "")) {
            c.createStatement().execute("INSERT INTO person (username,password,full_name,role) VALUES ('pdel','p','Pdel','PRESENTER')");
            var rs = c.createStatement().executeQuery("SELECT id FROM person WHERE username='pdel'");
            rs.next();
            int id = rs.getInt(1);
            c.createStatement().execute(String.format("INSERT INTO presenter (person_id, presenter_role, statistics) VALUES (%d, 'ROLE', '{}')", id));
        }

        PresenterDAOImpl dao = new PresenterDAOImpl();
        Optional<Presenter> before = dao.findByUsername("pdel");
        assertTrue(before.isPresent());

        dao.delete(before.get().getId());

        Optional<Presenter> after = dao.findByUsername("pdel");
        assertTrue(after.isEmpty());
    }
}
