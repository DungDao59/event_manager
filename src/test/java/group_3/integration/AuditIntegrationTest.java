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
import group_3.service.RegistrationService.RegistrationService;
import group_3.service.RegistrationService.RegistrationServiceImpl;
import group_3.service.SystemHistoryService.SystemHistoryService;
import group_3.service.SystemHistoryService.SystemHistoryServiceImpl;
import group_3.util.PasswordUtil;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuditIntegrationTest {
    private static AuthService authService;
    private static EventAdminService eventAdminService;
    private static RegistrationService registrationService;
    private static SystemHistoryService historyService;
    private static PersonDAO personDAO;

    private static int adminId;
    private static int attendeeId;
    private static int eventId;
    private static int sessionId;

    static void setup(){
        authService = new AuthServiceImpl();
        eventAdminService = new EventAdminServiceImpl();
        registrationService = new RegistrationServiceImpl();
        historyService = new SystemHistoryServiceImpl();
        personDAO = new PersonDAOImpl();

        // Create admin
        Person admin = new Admin();
        admin.setUsername("admin_test");
        admin.setPasswordHash(PasswordUtil.hash("admin123"));
        admin.setFullName("System Admin");
        admin.setDateOfBirth(LocalDate.of(1990,1,1));
        admin.setContactInformation("{\"email\":\"admin@test.com\"}");

        personDAO.create(admin);

        // Login
        AuthService authService = new AuthServiceImpl();
        assertTrue(authService.login("admin_test","admin123").isPresent());

        AuthContext.setCurrentUser(authService.getCurrentUser());

        //Create Event
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
