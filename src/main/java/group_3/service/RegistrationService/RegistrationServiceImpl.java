package group_3.service.RegistrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.ScheduleDAO;
import group_3.dao.SessionDAO;
import group_3.dao.TicketDAO;
import group_3.dao.impl.ScheduleDAOImpl;
import group_3.dao.impl.SessionDAOImpl;
import group_3.dao.impl.TicketDAOImpl;
import group_3.model.ScheduleEntry;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;
import group_3.util.QRCode;

public class RegistrationServiceImpl implements RegistrationService{
    private final TicketDAO ticketDAO =  new TicketDAOImpl();
    private final ScheduleDAO scheduleDAO = new ScheduleDAOImpl();
    private final SessionDAO sessionDAO = new SessionDAOImpl();

    @Override
    public boolean checkPersonConflict (int PersonId, int newSessionId) {
        ArrayList<ScheduleEntry> PersonSchedule = scheduleDAO.findAllScheduleByUserId(PersonId);
        Optional<Session> sessionOptional = sessionDAO.findById(newSessionId);
        Session s = sessionOptional.get();

        for (int i = 0; i < PersonSchedule.size(); i++) {
            ScheduleEntry currentEntry = PersonSchedule.get(i);
            if (s.getStartTime().isAfter(currentEntry.getStartTime()) && s.getStartTime().isBefore(currentEntry.getEndTime())) { //checking session start in the middle of assigned session
                return true; //there is conflict
            } if (s.getEndTime().isAfter(currentEntry.getStartTime()) && s.getEndTime().isBefore(currentEntry.getEndTime())) { //checking session end in the middle of assigned session
                return true; //there is conflict
            } if (s.getStartTime().isBefore(currentEntry.getStartTime()) && s.getEndTime().isAfter(currentEntry.getEndTime())) { //checking session overlap the assigned
                return true; //there is conflict
            }
        } return false;
    }

    @Override
    public boolean registerAttendee(int AttendeeId, int newSessionId, TicketType ticketType, double ticketPrice) {
        if (checkPersonConflict (AttendeeId, newSessionId)) {return false;}
        Optional<Session> sessionOptional = sessionDAO.findById(newSessionId);
        Session s = sessionOptional.get();

        Ticket ticket = new Ticket();
        ticket.setSessionID(newSessionId);
        ticket.setAttendeeID(AttendeeId);
        ticket.setEventID(s.getEventId());
        ticket.setPrice(ticketPrice);
        ticket.setType(ticketType);
        ticket.setQRpath(""); //temporarily hold blank value
        ticket.setStatus(TicketStatus.ACTIVE);

        int TicketID = ticketDAO.create(ticket); //create new ticket, hold the return generated ID

        if (TicketID == -1) {
            return false;
        }

        String qrPayload = QRCode.generateTicketQRPayload(ticket); //generate QRpath
        ticket.setQRpath(qrPayload);
        ticketDAO.update(ticket); //update into the database

        ScheduleEntry entry = new ScheduleEntry();
        entry.setSessionID(newSessionId);
        entry.setPersonID(AttendeeId);
        entry.setStartTime(s.getStartTime());
        entry.setEndTime(s.getEndTime());

        scheduleDAO.create(entry); //create schedule entry for the attendee
        return true;
    }

    @Override
    public boolean cancelRegistration(int ticketId) {
        Ticket ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            return false;
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketDAO.update(ticket);

        scheduleDAO.deleteByUserAndSession(ticket.getAttendeeID(), ticket.getSessionID());
        return true;
    }

    @Override
    public List<Ticket> getTicketsForAttendee(int attendeeId) {
        return ticketDAO.findTicketByAttendeeId(attendeeId);
    }

}
