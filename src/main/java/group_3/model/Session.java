package group_3.model;

/**
 * Session within an event, scheduled at a specific date/time and venue.
 * Maintains ids of assigned presenters for lightweight linkage.
 *
 * Author: <Tram Anh Tuan - s4075376>
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple Session class representing a specific session within an event. */
public class Session {
    private String sessionId;
    private String title;
    private String description;
    private LocalDateTime scheduledDateTime;
    private String venue;
    private int capacity;
    private final List<String> presenterIds = new ArrayList<>();

    public Session(String sessionId, String title, String description, LocalDateTime scheduledDateTime, String venue,
            int capacity) {
        this.sessionId = sessionId;
        this.title = title;
        this.description = description;
        this.scheduledDateTime = scheduledDateTime;
        this.venue = venue;
        this.capacity = capacity;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Presenters management
    /**
     * Add a presenter id to this session. Ignores null/empty; allows duplicates.
     * @param presenterId presenter identifier
     */
    public void addPresenter(String presenterId) {
        if (presenterId != null && !presenterId.isEmpty()) {
            presenterIds.add(presenterId);
        }
    }

    /**
     * Remove a presenter id from this session. No-op if not present.
     * @param presenterId presenter identifier
     */
    public void removePresenter(String presenterId) {
        presenterIds.remove(presenterId);
    }

    /** Snapshot of presenter ids assigned to this session (unmodifiable). */
    public List<String> getPresenterIds() {
        return Collections.unmodifiableList(presenterIds);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", scheduledDateTime=" + scheduledDateTime +
                ", venue='" + venue + '\'' +
                ", capacity=" + capacity +
                ", presenters=" + presenterIds +
                '}';
    }
}
