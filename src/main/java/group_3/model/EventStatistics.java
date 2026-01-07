package group_3.model;

/**
 * @author Group 3
 *
 * Data Transfer Object for Event Statistics.
 * Contains computed statistics for an event including revenue and attendance.
 */

public class EventStatistics {
    private int eventId;
    private String eventName;
    private double totalRevenue;
    private int totalTicketsSold;
    private int totalCheckedIn;
    private double attendanceRate; // percentage of checked-in vs sold tickets
    
    public EventStatistics() {
    }
    
    public EventStatistics(int eventId, String eventName, double totalRevenue, 
                          int totalTicketsSold, int totalCheckedIn) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.totalCheckedIn = totalCheckedIn;
        this.attendanceRate = calculateAttendanceRate();
    }
    
    private double calculateAttendanceRate() {
        if (totalTicketsSold == 0) {
            return 0.0;
        }
        return (totalCheckedIn * 100.0) / totalTicketsSold;
    }
    
    // Getters and Setters
    
    public int getEventId() {
        return eventId;
    }
    
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public int getTotalTicketsSold() {
        return totalTicketsSold;
    }
    
    public void setTotalTicketsSold(int totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
        this.attendanceRate = calculateAttendanceRate();
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
    
    @Override
    public String toString() {
        return "EventStatistics{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", totalTicketsSold=" + totalTicketsSold +
                ", totalCheckedIn=" + totalCheckedIn +
                ", attendanceRate=" + String.format("%.2f", attendanceRate) + "%" +
                '}';
    }
}
