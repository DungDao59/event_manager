package group_3.service.RegistrationService;

import group_3.dao.*;
import group_3.dao.impl.*;
import group_3.model.*;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;

import java.util.ArrayList;

public class RegistrationServiceImpl implements RegistrationService{
    private final TicketDAO ticketDAO =  new TicketDAOImpl();
    private final ScheduleDAO scheduleDAO = new ScheduleDAOImpl();

    public boolean checkPersonConflict (int PersonId, int newSessionId) {
        ArrayList<Schedule_entry> PersonSchedule = scheduleDAO.findAllScheduleByUserId(PersonId);
        Session s = getSessionById(newSessionId);
        for (int i = 0; i < PersonSchedule.size(); i++) {
            Schedule_entry currentEntry = PersonSchedule.get(i);
            if (s.getStartDateTime().isAfter(currentEntry.getStartDateTime()) &&
                    s.getStartDateTime().isBefore(currentEntry.getEndDateTime())) { //checking session start in the middle of assigned session
                return true; //there is conflict
            } if (s.getEndDateTime().isAfter(currentEntry.getStartDateTime()) &&
                    s.getEndDateTime().isBefore(currentEntry.getEndDateTime())) { //checking session end in the middle of assigned session
                return true; //there is conflict
            } if (s.getStartDateTime().isBefore(currentEntry.getStartDateTime()) && //checking session overlap the assigned session
                    s.getEndDateTime().isAfter(currentEntry.getEndDateTime())) {
                return true; //there is conflict
            }
        } return false;
    }

    @Override
    public boolean registerAttendee(int AttendeeId, int newSessionId, TicketType ticketType, double ticketPrice) {
        if (checkPersonConflict (AttendeeId, newSessionId)) {return false;}
        Session s = getSessionById(newSessionId);

        Ticket ticket = new Ticket();
        ticket.setSessionID(newSessionId);
        ticket.setAttendeeID(AttendeeId);
        ticket.setEventID(s.eventId);
        ticket.setPrice(ticketPrice);
        ticket.setType(ticketType);
        String qrPath = generateTicketCode(AttendeeId, newSessionId);
        ticket.setQRpath(qrPath);
        ticketDAO.create(ticket);

        Schedule_entry entry = new Schedule_entry();
        entry.setSessionID(newSessionId);
        entry.setPersonID(AttendeeId);
        entry.setStartTime(s.getStartTime());
        entry.setEndTime(s.getEndTime());

        scheduleDAO.create(entry);
        return true;
    }

    @Override
    public boolean cancelTicket(int id)  {
        Ticket ticket = ticketDAO.findById(id);
        if (ticket == null) {
            return false;
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketDAO.update(ticket);

        scheduleDAO.deleteByUserAndSession(ticket.getAttendeeID(), ticket.getSessionID());
        return true;
    }

    public static String generateTicketCode(int attendeeId, int sessionId) {
        return "TKT-" + attendeeId + "-" + sessionId + "-" + System.currentTimeMillis();
    }
}
