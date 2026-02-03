package group_3.service.SystemHistoryService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import group_3.dao.SystemHistoryDAO;
import group_3.model.SystemHistory;

class SystemHistoryServiceImplTest {

    private SystemHistoryServiceImpl service;
    private SystemHistoryDAO daoMock;

    @BeforeEach
    void setUp() {
        daoMock = mock(SystemHistoryDAO.class);
        service = new SystemHistoryServiceImpl(daoMock);
    }

    @Test
    void logAction_invokesDaoWithTimestamp() {
        service.logAction(1, "OP", "details");
        verify(daoMock).create(argThat(h -> h.getOperationType().equals("OP") && h.getTimestamp() != null));
    }

    @Test
    void getHistoryByDateRange_validatesInputs() {
        assertThrows(IllegalArgumentException.class, () -> service.getHistoryByDateRange(null, LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> service.getHistoryByDateRange(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void getHistoryByOperationType_validates() {
        assertThrows(IllegalArgumentException.class, () -> service.getHistoryByOperationType(null));
        assertThrows(IllegalArgumentException.class, () -> service.getHistoryByOperationType("  "));
    }

    @Test
    void getFilteredHistory_filtersCorrectly() {
        SystemHistory h1 = new SystemHistory(); h1.setUserId(1); h1.setOperationType("A"); h1.setTimestamp(OffsetDateTime.now());
        SystemHistory h2 = new SystemHistory(); h2.setUserId(2); h2.setOperationType("B"); h2.setTimestamp(OffsetDateTime.now().minusDays(5));
        when(daoMock.findAll()).thenReturn(List.of(h1, h2));

        var res1 = service.getFilteredHistory(1, null, null, null);
        assertEquals(1, res1.size());

        var res2 = service.getFilteredHistory(null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), null);
        assertEquals(1, res2.size());

        var res3 = service.getFilteredHistory(null, null, null, "B");
        assertEquals(1, res3.size());
    }
}
