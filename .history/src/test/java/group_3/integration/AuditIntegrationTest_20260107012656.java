package group_3.integration;

import group_3.dao.PersonDAO;
import group_3.dao.impl.PersonDAOImpl;
import group_3.model.Admin;
import group_3.model.Event;
import group_3.model.Person;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.security.AuthContext;
import group_3.service.AuthService.AuthService;
import group_3.service.AuthService.AuthServiceImpl;
import group_3.service.EventAdminService.EventAdminService;
import group_3.service.EventAdminService.EventAdminServiceImpl;
import group_3.util.PasswordUtil;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuditIntegrationTest {

    @Test
    void testAuditIntegration() {
        AuthService authService = new AuthServiceImpl();
        PersonDAO personDAO = new PersonDAOImpl();

        // Create admin
        Person admin = new Admin();
        admin.setUsername("admin_test_" + System.currentTimeMillis());
        admin.setPasswordHash(PasswordUtil.hash("admin123"));
        admin.setFullName("System Admin");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setContactInformation("{\"email\":\"admin@test.com\"}");

        personDAO.create(admin);

        // Login
        assertTrue(authService.login(admin.getUsername(), "admin123").isPresent());
        AuthContext.setCurrentUser(authService.getCurrentUser());

        // Create Event
        EventAdminService eventService = new EventAdminServiceImpl();

        Event event = new Event();
        event.setName("Tech conference");
        event.setType(EventType.CONFERENCE);
        event.setLocation("HCM");
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setStatus(EventStatus.SCHEDULED);

        Event created = eventService.createEvent(event);

        assertNotNull(created);

        // Clean up
        AuthContext.clear();
        authService.logout();
    }
}
