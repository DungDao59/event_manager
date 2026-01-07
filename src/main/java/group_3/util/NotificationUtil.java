package group_3.util;

import group_3.model.Person;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Group 3
 *
 * Utility class for sending console-based notifications to users
 * with timestamp and user information.
 */


public class NotificationUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private NotificationUtil(){}

    public static void notify(Person user, String title, String message){
        if(user == null) return;

        String timestamp = OffsetDateTime.now().format(formatter);

        System.out.println("========================================");
        System.out.println("NOTIFICATION");
        System.out.println("Time : " + timestamp);
        System.out.println("User : " + user.getUsername());
        System.out.println("Role : " + user.getRole());
        System.out.println("Title : " + title);
        System.out.println("Message : " + message);
        System.out.println("========================================");

    }

    public static void notifyRegistrationSuccess(Person user, int sessionId){
        notify(user,
                "Registration Successful",
                "You have successfully registered for session ID: " + sessionId);
    }

    public static void notifyRegistrationCancelled(Person user, int ticketId){
        notify(
                user,
                "Registration Cancelled",
                "Your ticket (ID: " + ticketId + " ) has been cancelled"
        );
    }

    public static void notifyReportExported(Person user, String reportType){
        notify(
                user,
                "Report exported",
                "A " + reportType + " report has been exported successfully"
        );
    }
}
