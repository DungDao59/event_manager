package group_3.model;

/*
 * Session within an event, scheduled at a specific date/time and venue.
 * Maintains ids of assigned presenters for lightweight linkage.
 * Author: <Tram Anh Tuan - s4075376>
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple Session class representing a specific session within an event. */
public class Session {
    private int sessionId;
    private int eventId; // Reference to parent event
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String venue;
    private int capacity;
    private final List<Integer> presenterIds = new ArrayList<>();
    private final List<Integer> materialIds = new ArrayList<>(); // References to session materials

    /**
     * Full constructor with all attributes.
     */
    public Session(int sessionId, int eventId, String title, String description,
                   LocalDateTime startTime, LocalDateTime endTime,
                   String venue, int capacity) {
        this.sessionId = sessionId;
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.capacity = capacity;
    }

    
    // Getters and Setters

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
    public void addPresenter(int presenterId) {
        if (presenterId > 0) {
            presenterIds.add(presenterId);
        }
    }

    /**
     * Remove a presenter id from this session. No-op if not present.
     * @param presenterId presenter identifier
     */
    public void removePresenter(int presenterId) {
        presenterIds.remove(presenterId);
    }

    /** Snapshot of presenter ids assigned to this session (unmodifiable). */
    public List<Integer> getPresenterIds() {
        return Collections.unmodifiableList(presenterIds);
    }

    // Session materials management

    /**
     * Add a material id to this session. Ignores null/empty.
     * @param materialId material identifier
     */
    public void addMaterial(int materialId) {
        if (materialId > 0) {
            materialIds.add(materialId);
        }
    }

    /**
     * Remove a material id from this session. No-op if not present.
     * @param materialId material identifier
     */
    public void removeMaterial(int materialId) {
        materialIds.remove(materialId);
    }

    /** Snapshot of material ids assigned to this session (unmodifiable). */
    public List<Integer> getMaterialIds() {
        return Collections.unmodifiableList(materialIds);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", venue='" + venue + '\'' +
                ", capacity=" + capacity +
                ", presenters=" + presenterIds +
                ", materials=" + materialIds +
                '}';
    }
}
