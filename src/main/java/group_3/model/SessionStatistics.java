package group_3.model;

/**
 * @author Group 3
 *
 * Data Transfer Object for Session Statistics.
 * Contains computed statistics for a session including attendance and popularity metrics.
 */
public class SessionStatistics {
    private int sessionId;
    private String sessionTitle;
    private int eventId;
    private int totalTicketsSold;
    private int totalCheckedIn;
    private double attendanceRate; // percentage of checked-in vs sold tickets
    private int capacity;
    private double capacityUtilization; // percentage of capacity filled
    
    public SessionStatistics() {
    }
    
    public SessionStatistics(int sessionId, String sessionTitle, int eventId, 
                            int totalTicketsSold, int totalCheckedIn, int capacity) {
        this.sessionId = sessionId;
        this.sessionTitle = sessionTitle;
        this.eventId = eventId;
        this.totalTicketsSold = totalTicketsSold;
        this.totalCheckedIn = totalCheckedIn;
        this.capacity = capacity;
        this.attendanceRate = calculateAttendanceRate();
        this.capacityUtilization = calculateCapacityUtilization();
    }
    
    private double calculateAttendanceRate() {
        if (totalTicketsSold == 0) {
            return 0.0;
        }
        return (totalCheckedIn * 100.0) / totalTicketsSold;
    }
    
    private double calculateCapacityUtilization() {
        if (capacity == 0) {
            return 0.0;
        }
        return (totalTicketsSold * 100.0) / capacity;
    }
    
    // Getters and Setters
    
    public int getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getSessionTitle() {
        return sessionTitle;
    }
    
    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public int getTotalTicketsSold() {
        return totalTicketsSold;
    }
    
    public void setTotalTicketsSold(int totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
        this.attendanceRate = calculateAttendanceRate();
        this.capacityUtilization = calculateCapacityUtilization();
    }
    
    public int getTotalCheckedIn() {
        return totalCheckedIn;
    }
    
    public void setTotalCheckedIn(int totalCheckedIn) {
        this.totalCheckedIn = totalCheckedIn;
        this.attendanceRate = calculateAttendanceRate();
    }
    
    public double getAttendanceRate() {
        return attendanceRate;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        this.capacityUtilization = calculateCapacityUtilization();
    }
    
    public double getCapacityUtilization() {
        return capacityUtilization;
    }
    
    @Override
    public String toString() {
        return "SessionStatistics{" +
                "sessionId=" + sessionId +
                ", sessionTitle='" + sessionTitle + '\'' +
                ", eventId=" + eventId +
                ", totalTicketsSold=" + totalTicketsSold +
                ", totalCheckedIn=" + totalCheckedIn +
                ", attendanceRate=" + String.format("%.2f", attendanceRate) + "%" +
                ", capacity=" + capacity +
                ", capacityUtilization=" + String.format("%.2f", capacityUtilization) + "%" +
                '}';
    }
}
