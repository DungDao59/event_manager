package group_3.dao.impl;

import group_3.dao.EventDAO;
import group_3.model.Event;
import group_3.util.MockData;

import java.util.*;

/** In-memory implementation of EventDAO for mock mode. */
public class EventDAOInMemory implements EventDAO {

    @Override
    public void create(Event event) {
        int id;
        try {
            id = event.getEventId() != null ? Integer.parseInt(event.getEventId()) : 0;
        } catch (NumberFormatException e) {
            id = 0;
        }
        if (id <= 0 || MockData.EVENTS.containsKey(id)) {
            id = MockData.EVENT_SEQ.incrementAndGet();
        }
        event.setEventId(String.valueOf(id));
        MockData.EVENTS.put(id, event);
    }

    @Override
    public Optional<Event> findById(int eventId) {
        return Optional.ofNullable(MockData.EVENTS.get(eventId));
    }

    @Override
    public Optional<Event> findByName(String name) {
        return MockData.EVENTS.values().stream()
                .filter(e -> e.getName() != null && e.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(MockData.EVENTS.values());
    }

    @Override
    public void update(Event event) {
        if (event.getEventId() == null) return;
        try {
            int id = Integer.parseInt(event.getEventId());
            MockData.EVENTS.put(id, event);
        } catch (NumberFormatException ignored) { }
    }

    @Override
    public void delete(int eventId) {
        MockData.EVENTS.remove(eventId);
        // Also cascade delete sessions for this event
        MockData.SESSIONS.values().removeIf(s -> {
            try { return Integer.parseInt(s.getEventId()) == eventId; } catch (Exception e) { return false; }
        });
        // Tickets filtered by event
        MockData.TICKETS.values().removeIf(t -> t.getEventID() == eventId);
    }

    @Override
    public int count() {
        return MockData.EVENTS.size();
    }

    @Override
    public boolean exists(int eventId) {
        return MockData.EVENTS.containsKey(eventId);
    }
}
