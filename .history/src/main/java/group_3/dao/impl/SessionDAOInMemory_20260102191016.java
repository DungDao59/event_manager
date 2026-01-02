package group_3.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import group_3.dao.SessionDAO;
import group_3.model.Session;
import group_3.util.MockData;

/** In-memory implementation of SessionDAO for mock mode. */
public class SessionDAOInMemory implements SessionDAO {
    @Override
    public void create(Session session) {
        int id = session.getSessionId();
        if (id <= 0 || MockData.SESSIONS.containsKey(id)) {
            id = MockData.SESSION_SEQ.incrementAndGet();
        }
        session.setSessionId(id);
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
                .filter(s -> s.getEventId() == eventId)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Session session) {
        int id = session.getSessionId();
        if (id <= 0) return;
        MockData.SESSIONS.put(id, session);
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
                    .filter(s -> s.getEventId() == eventId)
                    .count();
        }
    }
