package group_3.service.ScheduleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.model.ScheduleEntry;
import group_3.model.Session;

class ScheduleServiceImplTest {

    private ScheduleServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        service = new ScheduleServiceImpl();
        ScheduleDAO scheduleDAOMock = mock(ScheduleDAO.class);
        SessionDAO sessionDAOMock = mock(SessionDAO.class);
        setField(service, "scheduleDAO", scheduleDAOMock);
        setField(service, "sessionDAO", sessionDAOMock);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void hasConflict_by_times_and_session() {
        ScheduleDAO scheduleDAOMock = (ScheduleDAO) getField(service, "scheduleDAO");
        ScheduleEntry e = new ScheduleEntry(1, 1, 5, LocalDateTime.of(2025,1,1,10,0), LocalDateTime.of(2025,1,1,11,0));
        when(scheduleDAOMock.findAllScheduleByUserId(5)).thenReturn(new ArrayList<ScheduleEntry>() {{ add(e); }});

        assertTrue(service.hasConflict(5, LocalDateTime.of(2025,1,1,10,30), LocalDateTime.of(2025,1,1,11,30)));

        Session s = new Session(2, 1, "T", "D", LocalDateTime.of(2025,1,2,10,0), LocalDateTime.of(2025,1,2,11,0), "V", 10);
        SessionDAO sessionDAOMock = (SessionDAO) getField(service, "sessionDAO");
        when(sessionDAOMock.findById(2)).thenReturn(Optional.of(s));

        assertFalse(service.hasConflict(5, 2));
    }

    private Object getField(Object target, String name) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
