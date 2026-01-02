package group_3.dao;

import group_3.model.Person;
import group_3.model.Ticket;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TicketDAO {
    void create(Ticket ticket);
    void update(Ticket ticket);
    boolean delete(int id);
    ArrayList<Ticket> findAll();
    Ticket findById(int id);
    ArrayList<Ticket> findTicketByAttendeeId(int id);
    ArrayList<Ticket> findTicketBySessionId(int id);
}
