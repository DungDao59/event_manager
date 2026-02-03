package group_3.service.PresenterService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.PresenterDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.model.Presenter;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.TicketStatus;

class PresenterServiceImplTest {

    private PresenterServiceImpl service;
    private PresenterDAO presenterDAOMock;
    private SessionDAO sessionDAOMock;
    private TicketDAO ticketDAOMock;

    @BeforeEach
    void setUp() {
        presenterDAOMock = mock(PresenterDAO.class);
        sessionDAOMock = mock(SessionDAO.class);
        ticketDAOMock = mock(TicketDAO.class);
        service = new PresenterServiceImpl(presenterDAOMock, sessionDAOMock, ticketDAOMock);
    }

    @Test
    void createPresenter_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createPresenter(null));
    }

    @Test
    void createPresenter_success_returnsFound() {
        Presenter p = new Presenter(0, "u", "p", "F", java.time.LocalDate.now(), "", "", "");
        when(presenterDAOMock.create(p)).thenReturn(10);
        Presenter saved = new Presenter(10, "u", "p", "F", java.time.LocalDate.now(), "", "", "");
        when(presenterDAOMock.findById(10)).thenReturn(Optional.of(saved));

        Presenter res = service.createPresenter(p);
        assertEquals(10, res.getId());
    }

    @Test
    void getPresenterByUsername_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getPresenterByUsername(""));
    }

    @Test
    void updatePersonalInfo_notFound_throws() {
        when(presenterDAOMock.findById(5)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.updatePersonalInfo(5, "N", "C"));
    }

    @Test
    void updatePersonalInfo_updatesFields() {
        Presenter p = new Presenter(6, "u6", "p", "Old", java.time.LocalDate.now(), "oldC", "", "");
        when(presenterDAOMock.findById(6)).thenReturn(Optional.of(p));

        service.updatePersonalInfo(6, "NewName", "NewContact");
        assertEquals("NewName", p.getFullName());
        assertEquals("NewContact", p.getContactInformation());
        verify(presenterDAOMock).update(p);
    }

    @Test
    void getSessionsPresented_usingPresenterIds() {
        Session s1 = new Session(101, 1, "S1", "d", null, null, "V", 10);
        s1.setPresenterIds(new ArrayList<Integer>() {{ add(20); }});
        Session s2 = new Session(102, 1, "S2", "d", null, null, "V", 10);
        s2.setPresenterIds(new ArrayList<Integer>() {{ add(30); }});
        when(sessionDAOMock.findAll()).thenReturn(List.of(s1, s2));

        int count = service.getSessionsPresented(20);
        assertEquals(1, count);
    }

    @Test
    void getSessionsPresented_fallback_modulo() {
        // presenter position should be computed from presenterDAO.findAll order
        Presenter p11 = new Presenter(11, "p1", "p", "N", java.time.LocalDate.now(), "", "", "");
        Presenter p12 = new Presenter(12, "p2", "p", "N", java.time.LocalDate.now(), "", "", "");
        Presenter p13 = new Presenter(13, "p3", "p", "N", java.time.LocalDate.now(), "", "", "");
        Presenter p14 = new Presenter(14, "p4", "p", "N", java.time.LocalDate.now(), "", "", "");
        Presenter p15 = new Presenter(15, "p5", "p", "N", java.time.LocalDate.now(), "", "", "");

        when(presenterDAOMock.findAll()).thenReturn(List.of(p11, p12, p13, p14, p15));

        // create sessions with ids so (sessionId % 5) gives positions 0..4
        Session s1 = new Session(21, 1, "S1", "d", null, null, "V", 10); // 21%5 = 1
        Session s2 = new Session(22, 1, "S2", "d", null, null, "V", 10); // 22%5 = 2
        Session s3 = new Session(23, 1, "S3", "d", null, null, "V", 10); // 23%5 = 3

        when(sessionDAOMock.findAll()).thenReturn(List.of(s1, s2, s3));

        int c = service.getSessionsPresented(12); // presenter 12 is at index 1 => position 1
        assertEquals(1, c);
    }

    @Test
    void getTotalAttendeesPresented_countsActiveAndUsed() {
        // sessions list
        when(sessionDAOMock.findAll()).thenReturn(List.of(new Session(1,1,"S","d",null,null,"v",10)));
        // getSessionsList will find via presenterIds fallback; simulate getSessionsList by stubbing sessionDAO.findAll to include presenter id
        Session s = new Session(1,1,"S","d",null,null,"v",10);
        s.setPresenterIds(new ArrayList<Integer>() {{ add(50); }});
        when(sessionDAOMock.findAll()).thenReturn(List.of(s));

        Ticket t1 = new Ticket(); t1.setStatus(TicketStatus.ACTIVE);
        Ticket t2 = new Ticket(); t2.setStatus(TicketStatus.USED);
        Ticket t3 = new Ticket(); t3.setStatus(TicketStatus.CANCELLED);
        when(ticketDAOMock.findTicketBySessionId(1)).thenReturn(new ArrayList<Ticket>() {{ add(t1); add(t2); add(t3); }});

        int total = service.getTotalAttendeesPresented(50);
        assertEquals(2, total);
    }

    @Test
    void getAverageAttendance_handles_zeroSessions() {
        when(sessionDAOMock.findAll()).thenReturn(List.of());
        assertEquals(0.0, service.getAverageAttendance(99));
    }

    @Test
    void calculateAndUpdateStatistics_updatesPresenter() {
        Presenter p = new Presenter(200, "u", "p", "N", java.time.LocalDate.now(), "", "", "");
        when(presenterDAOMock.findById(200)).thenReturn(Optional.of(p));
        // make sessions and tickets empty so stats will be zeros
        when(sessionDAOMock.findAll()).thenReturn(List.of());
        service.calculateAndUpdateStatistics(200);
        verify(presenterDAOMock).update(p);
        assertNotNull(p.getStatistics());
    }

    @Test
    void calculateAndUpdateStatistics_notFound_throws() {
        when(presenterDAOMock.findById(300)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.calculateAndUpdateStatistics(300));
    }

    @Test
    void getPresenterStatistics_containsKeys() {
        when(sessionDAOMock.findAll()).thenReturn(List.of());
        when(presenterDAOMock.findAll()).thenReturn(List.of());
        Map<String, Object> stats = service.getPresenterStatistics(5);
        assertTrue(stats.containsKey("presenter_id"));
        assertTrue(stats.containsKey("sessions_presented"));
    }
}
