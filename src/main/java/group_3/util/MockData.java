package group_3.util;

import group_3.model.Event;
import group_3.model.Presenter;
import group_3.model.Session;
import group_3.model.Ticket;
import group_3.model.enums.EventStatus;
import group_3.model.enums.EventType;
import group_3.model.enums.Role;
import group_3.model.enums.TicketStatus;
import group_3.model.enums.TicketType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared in-memory mock storage for DAOInMemory classes.
 */
public final class MockData {
    private MockData() {}

    public static final Map<Integer, Event> EVENTS = new ConcurrentHashMap<>();
    public static final Map<Integer, Session> SESSIONS = new ConcurrentHashMap<>();
    public static final Map<Integer, Ticket> TICKETS = new ConcurrentHashMap<>();
    public static final Map<Integer, Presenter> PRESENTERS = new ConcurrentHashMap<>();

    public static final AtomicInteger EVENT_SEQ = new AtomicInteger(1000);
    public static final AtomicInteger SESSION_SEQ = new AtomicInteger(5000);
    public static final AtomicInteger TICKET_SEQ = new AtomicInteger(8000);
    public static final AtomicInteger PRESENTER_SEQ = new AtomicInteger(3000);

    static {
        seed();
    }

    private static void seed() {
        // Seed Presenters
        int p1 = nextPresenter("alice_smith", "Alice Smith", "Lead Architect", "12 years in tech");
        int p2 = nextPresenter("bob_johnson", "Bob Johnson", "Java Expert", "8 years in Java");
        int p3 = nextPresenter("carol_chen", "Carol Chen", "JavaFX Specialist", "5 years UI development");
        int p4 = nextPresenter("dave_lee", "Dave Lee", "AI/ML Engineer", "7 years in AI");

        // Seed Events
        int e1 = nextEvent("Tech Summit 2025", EventType.CONFERENCE, "Hanoi", 2, EventStatus.SCHEDULED);
        int e2 = nextEvent("JavaFX Workshop", EventType.WORKSHOP, "HCMC", 1, EventStatus.SCHEDULED);
        int e3 = nextEvent("Product Launch", EventType.CONCERT, "Online", 1, EventStatus.CANCELLED);
        int e4 = nextEvent("AI Exhibition", EventType.EXHIBITION, "Danang", 3, EventStatus.SCHEDULED);

        // Seed Sessions (tie to events)
        int s1 = nextSession(e1, "Keynote: Future of Tech", 300);
        addPresenterToSession(s1, p1); // Alice presents
        
        int s2 = nextSession(e1, "Cloud Native Patterns", 120);
        addPresenterToSession(s2, p2); // Bob presents
        
        int s3 = nextSession(e2, "Hands-on JavaFX", 40);
        addPresenterToSession(s3, p3); // Carol presents
        
        int s4 = nextSession(e4, "AI Demos", 200);
        addPresenterToSession(s4, p4); // Dave presents
        
        int s5 = nextSession(e4, "Robotics Showcase", 150);
        addPresenterToSession(s5, p4); // Dave also presents

        // Seed Tickets (some used, some active)
        addTickets(e1, s1, 200, 149.0, 120, 60); // 120 used, 60 active
        addTickets(e1, s2, 80, 99.0, 50, 20);
        addTickets(e2, s3, 25, 49.0, 10, 10);
        addTickets(e4, s4, 90, 39.0, 30, 40);
        addTickets(e4, s5, 60, 39.0, 20, 25);
    }

    private static int nextPresenter(String username, String fullName, String role, String stats) {
        int id = PRESENTER_SEQ.incrementAndGet();
        Presenter p = new Presenter(id, username, "hashed_pwd", fullName, LocalDate.now().minusYears(30), "contact@example.com", role, stats);
        PRESENTERS.put(id, p);
        return id;
    }

    private static int nextEvent(String name, EventType type, String location, int durationDays, EventStatus status) {
        int id = EVENT_SEQ.incrementAndGet();
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = start.plusDays(durationDays);
        // Placeholder image URL (public domain image)
        String imageUrl = "https://via.placeholder.com/600x300?text=" + name.replace(" ", "+");
        Event ev = new Event(String.valueOf(id), name, type, start, end, location, durationDays, status, imageUrl);
        EVENTS.put(id, ev);
        return id;
    }

    private static int nextSession(int eventId, String title, int capacity) {
        int id = SESSION_SEQ.incrementAndGet();
        LocalDateTime start = LocalDateTime.now().plusDays(3).withHour(9);
        LocalDateTime end = start.plusHours(2);
        Session s = new Session(String.valueOf(id), String.valueOf(eventId), title, title + " description", start, end, "Hall A", capacity);
        SESSIONS.put(id, s);
        // Link to event
        Event ev = EVENTS.get(eventId);
        if (ev != null) {
            ev.addSession(String.valueOf(id));
        }
        return id;
    }

    private static void addPresenterToSession(int sessionId, int presenterId) {
        Session s = SESSIONS.get(sessionId);
        if (s != null) {
            s.addPresenter(String.valueOf(presenterId));
        }
    }

    private static void addTickets(int eventId, int sessionId, int count, double price, int used, int active) {
        for (int i = 0; i < count; i++) {
            int id = TICKET_SEQ.incrementAndGet();
            Ticket t = new Ticket(id, eventId, sessionId, 10000 + i, TicketType.GENERAL, price,
                    i < used ? TicketStatus.USED : (i < used + active ? TicketStatus.ACTIVE : TicketStatus.CANCELLED),
                    null);
            TICKETS.put(id, t);
        }
    }
}
