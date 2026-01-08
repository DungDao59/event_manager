package group_3.util;

import group_3.model.Person;

/**
 * @author Group 3
 *
 * Utility class for sending console-based notifications to users
 * with timestamp and user information.
 */


public class NotificationUtil {
    

    private NotificationUtil(){}

    public static void notify(Person user, String title, String message){
        if(user == null) return;

       

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
