package group_3.model;

/**
 * Event composed of one or more sessions occurring at a location and date/time.
 * Stores session ids rather than full session objects to keep the model light.
 *
 * Author: <Tram Anh Tuan - s4075376>
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple Event class representing an event with multiple sessions. */
public class Event {
    /** High-level event types. */
    public enum Type {
        CONFERENCE,
        WORKSHOP,
        CONCERT,
        EXHIBITION
    }

    private String eventId;
    private String name;
    private Type type;
    private LocalDateTime date;
    private String location;
    private int duration; // duration in days
    private final List<String> sessionIds = new ArrayList<>();

    public Event(String eventId, String name, Type type, LocalDateTime dt, String location, int duration) {
        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.date = dt;
        this.location = location;
        this.duration = duration;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void addSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            sessionIds.add(sessionId);
        }
    }

    /** Remove a session id from the event (no-op if not present). */
    public void removeSession(String sessionId) {
        sessionIds.remove(sessionId);
    }

    /** Snapshot of session ids linked to this event (unmodifiable). */
    public List<String> getSessionIds() {
        return Collections.unmodifiableList(sessionIds);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", duration=" + duration +
                ", sessions=" + sessionIds +
                '}';
    }
}
