package group_3.dao;

import java.util.ArrayList;

import group_3.model.Ticket;

public interface TicketDAO {
    int create(Ticket ticket);
    void update(Ticket ticket);
    boolean delete(int id);
    ArrayList<Ticket> findAll();
    Ticket findById(int id);
    ArrayList<Ticket> findTicketByAttendeeId(int id);
    ArrayList<Ticket> findTicketBySessionId(int id);
}
