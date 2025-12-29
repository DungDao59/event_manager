package group_3.dao.impl;

import group_3.dao.TicketDAO;
import group_3.model.Ticket;
import group_3.util.MockData;

import java.util.ArrayList;
import java.util.stream.Collectors;

/** In-memory implementation of TicketDAO for mock mode. */
public class TicketDAOInMemory implements TicketDAO {
    @Override
    public void create(Ticket ticket) {
        int id = MockData.TICKET_SEQ.incrementAndGet();
        ticket.setTicketID(id);
        MockData.TICKETS.put(id, ticket);
    }

    @Override
    public void update(Ticket ticket) {
        MockData.TICKETS.put(ticket.getTicketID(), ticket);
    }

    @Override
    public boolean delete(int id) {
        return MockData.TICKETS.remove(id) != null;
    }

    @Override
    public ArrayList<Ticket> findAll() {
        return new ArrayList<>(MockData.TICKETS.values());
    }

    @Override
    public Ticket findById(int id) {
        return MockData.TICKETS.get(id);
    }

    @Override
    public ArrayList<Ticket> findTicketByAttendeeId(int id) {
        return MockData.TICKETS.values().stream()
                .filter(t -> t.getAttendeeID() == id)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Ticket> findTicketBySessionId(int id) {
        return MockData.TICKETS.values().stream()
                .filter(t -> t.getSessionID() == id)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
