package group_3.dao.impl;

import group_3.dao.SessionDAO;
import group_3.model.Session;
import group_3.util.MockData;

import java.util.*;
import java.util.stream.Collectors;

/** In-memory implementation of SessionDAO for mock mode. */
public class SessionDAOInMemory implements SessionDAO {
    @Override
    public void create(Session session) {
        int id;
        try {
            id = session.getSessionId() != null ? Integer.parseInt(session.getSessionId()) : 0;
        } catch (NumberFormatException e) {
            id = 0;
        }
        if (id <= 0 || MockData.SESSIONS.containsKey(id)) {
            id = MockData.SESSION_SEQ.incrementAndGet();
        }
        session.setSessionId(String.valueOf(id));
        MockData.SESSIONS.put(id, session);
    }

    @Override
    public Optional<Session> findById(int sessionId) {
        return Optional.ofNullable(MockData.SESSIONS.get(sessionId));
    }

    @Override
    public Optional<Session> findByTitle(String title) {
        return MockData.SESSIONS.values().stream()
                .filter(s -> s.getTitle() != null && s.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public List<Session> findAll() {
        return new ArrayList<>(MockData.SESSIONS.values());
    }

    @Override
    public List<Session> findByEventId(int eventId) {
        return MockData.SESSIONS.values().stream()
                .filter(s -> {
                    try { return s.getEventId() != null && Integer.parseInt(s.getEventId()) == eventId; }
                    catch (Exception e) { return false; }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void update(Session session) {
        if (session.getSessionId() == null) return;
        try {
            int id = Integer.parseInt(session.getSessionId());
            MockData.SESSIONS.put(id, session);
        } catch (NumberFormatException ignored) { }
    }

    @Override
    public void delete(int sessionId) {
        MockData.SESSIONS.remove(sessionId);
        MockData.TICKETS.values().removeIf(t -> t.getSessionID() == sessionId);
    }

    @Override
    public int count() {
        return MockData.SESSIONS.size();
    }

    @Override
    public boolean exists(int sessionId) {
        return MockData.SESSIONS.containsKey(sessionId);
    }

    @Override
    public int countByEventId(int eventId) {
        return (int) MockData.SESSIONS.values().stream()
                .filter(s -> {
                    try { return s.getEventId() != null && Integer.parseInt(s.getEventId()) == eventId; }
                    catch (Exception e) { return false; }
                })
                .count();
    }
}
