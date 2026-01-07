package group_3.service.RegistrationService;

import java.util.List;

import group_3.model.Ticket;
import group_3.model.enums.TicketType;

/**
 * @author Group 3
 *
 * Service interface defining operations for attendee registration,
 * ticket management, and conflict checking.
 */


public interface RegistrationService {
    boolean checkPersonConflict(int attendeeId, int sessionId);

    boolean registerAttendee(int AttendeeId, int newSessionId, TicketType ticketType, double ticketPrice);

    boolean cancelRegistration(int ticketId);

    List<Ticket> getTicketsForAttendee( int attendeeId );
}
