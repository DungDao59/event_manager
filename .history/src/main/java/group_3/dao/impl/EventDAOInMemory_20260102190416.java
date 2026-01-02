package group_3.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group_3.dao.EventDAO;
import group_3.model.Event;
import group_3.util.MockData;

/** In-memory implementation of EventDAO for mock mode. */
public class EventDAOInMemory implements EventDAO {

    @Override
    public void create(Event event) {
        int id = event.getEventId();
        if (id <= 0 || MockData.EVENTS.containsKey(id)) {
            id = MockData.EVENT_SEQ.incrementAndGet();
        }
        event.setEventId(id);
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
        int id = event.getEventId();
        if (id <= 0) return;
        MockData.EVENTS.put(id, event);
    }

    @Override
    public void delete(int eventId) {
        MockData.EVENTS.remove(eventId);
        // Also cascade delete sessions for this event
        MockData.SESSIONS.values().removeIf(s -> s.getEventId() == eventId);
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
