package group_3.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import group_3.dao.impl.EventDAOImpl;
import group_3.model.Event;
import group_3.model.Session;
import group_3.util.DaoProvider;
import group_3.dao.SessionDAO;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;

class EventDAOImplTest {

    private EventDAOImpl dao;
    private MockedStatic<DaoProvider> daoProviderStatic;

    @BeforeEach
    void setUp() {
        dao = new EventDAOImpl();
    }

    @AfterEach
    void tearDown() {
        if (daoProviderStatic != null) daoProviderStatic.close();
    }

    @Test
    void mapRowToEvent_parsesRowAndLoadsSessions() throws Exception {
        // Use H2's SimpleResultSet to avoid mocking core JDBC interfaces (ByteBuddy issues on newer JDKs)
        org.h2.tools.SimpleResultSet rs = new org.h2.tools.SimpleResultSet();
        rs.addColumn("event_id", java.sql.Types.INTEGER, 10, 0);
        rs.addColumn("name", java.sql.Types.VARCHAR, 255, 0);
        rs.addColumn("type", java.sql.Types.VARCHAR, 100, 0);
        rs.addColumn("start_date", java.sql.Types.TIMESTAMP, 0, 0);
        rs.addColumn("end_date", java.sql.Types.TIMESTAMP, 0, 0);
        rs.addColumn("location", java.sql.Types.VARCHAR, 255, 0);
        rs.addColumn("duration", java.sql.Types.INTEGER, 10, 0);
        rs.addColumn("status", java.sql.Types.VARCHAR, 100, 0);
        rs.addColumn("event_image", java.sql.Types.VARCHAR, 255, 0);

        rs.addRow(1, "Test Event", "SUMMIT", Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 9, 0)), Timestamp.valueOf(LocalDateTime.of(2025, 5, 1, 17, 0)), "Hall B", 480, "ONGOING", "/tmp/img.png");
        rs.next();

        // Mock sessions returned by SessionDAO
        SessionDAO mockSessionDAO = mock(SessionDAO.class);
        List<Session> sessions = new ArrayList<>();
        Session s = new Session(111, 1, "T", "D",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), "V", 10);
        sessions.add(s);
        when(mockSessionDAO.findByEventId(1)).thenReturn(sessions);

        daoProviderStatic = mockStatic(DaoProvider.class);
        daoProviderStatic.when(DaoProvider::getSessionDAO).thenReturn(mockSessionDAO);

        Method method = EventDAOImpl.class.getDeclaredMethod("mapRowToEvent", ResultSet.class);
        method.setAccessible(true);

        Event e = (Event) method.invoke(dao, rs);

        assertEquals(1, e.getEventId());
        assertEquals("Test Event", e.getName());
        assertEquals(EventType.CONFERENCE, e.getType(), "SUMMIT should map to CONFERENCE");
        assertEquals(EventStatus.ONGOING, e.getStatus());
        assertTrue(e.getSessionIds().contains("111"));
    }

    @Test
    void mapRowToEvent_handlesUnknownStatusAndType() throws Exception {
        org.h2.tools.SimpleResultSet rs = new org.h2.tools.SimpleResultSet();
        rs.addColumn("event_id", java.sql.Types.INTEGER, 10, 0);
        rs.addColumn("name", java.sql.Types.VARCHAR, 255, 0);
        rs.addColumn("type", java.sql.Types.VARCHAR, 100, 0);
        rs.addColumn("start_date", java.sql.Types.TIMESTAMP, 0, 0);
        rs.addColumn("end_date", java.sql.Types.TIMESTAMP, 0, 0);
        rs.addColumn("location", java.sql.Types.VARCHAR, 255, 0);
        rs.addColumn("duration", java.sql.Types.INTEGER, 10, 0);
        rs.addColumn("status", java.sql.Types.VARCHAR, 100, 0);
        rs.addColumn("event_image", java.sql.Types.VARCHAR, 255, 0);

        rs.addRow(2, "Other", "UNKNOWN_TYPE", null, null, "Nowhere", 1, null, null);
        rs.next();

        // no sessions
        SessionDAO mockSessionDAO = mock(SessionDAO.class);
        when(mockSessionDAO.findByEventId(2)).thenReturn(new ArrayList<>());

        daoProviderStatic = mockStatic(DaoProvider.class);
        daoProviderStatic.when(DaoProvider::getSessionDAO).thenReturn(mockSessionDAO);

        Method method = EventDAOImpl.class.getDeclaredMethod("mapRowToEvent", ResultSet.class);
        method.setAccessible(true);

        Event e = (Event) method.invoke(dao, rs);

        assertEquals(EventType.CONFERENCE, e.getType(), "Unknown types should default to CONFERENCE");
        assertEquals(EventStatus.SCHEDULED, e.getStatus(), "null status should default to SCHEDULED");
    }
}
