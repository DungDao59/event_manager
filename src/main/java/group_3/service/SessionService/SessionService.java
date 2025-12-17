package group_3.service.SessionService;

import group_3.model.Session;
import java.util.*;

public interface SessionService {
    Session getSessionById(int sessionId);

    List<Session> getSessionByEvent(int eventId);

    List<Session> getAllSessions();
}
