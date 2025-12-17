package group_3.service.RegistrationService;

import group_3.model.Ticket;
import group_3.model.enums.TicketType;
import java.util.*;

public interface RegistrationService {
    boolean checkPersonConflict(int attendeeId, int sessionId, TicketType type, double price);

    boolean cancelRegistration(int ticketId);

    List<Ticket> getTicketsForAttendee( int attendeeId );
}
