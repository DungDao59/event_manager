# Event Statistics Service

## Overview

The Event Statistics Service provides comprehensive analytics for events and sessions, including revenue calculations, attendance tracking, and popularity metrics.

## Features

### 1. **Revenue Calculation**
- Calculate total revenue per event from ticket sales
- Only includes tickets with status `ACTIVE` or `USED`
- Excludes `CANCELLED` tickets from revenue calculations

### 2. **Attendance Rate Tracking**
- Track tickets sold vs. actual attendee check-ins
- Calculate attendance rate as percentage: `(Checked In / Tickets Sold) × 100`
- Supports both event-level and session-level attendance metrics

### 3. **Most Popular Sessions**
- Identify top sessions by ticket sales
- Rank sessions within a specific event or globally
- Includes capacity utilization metrics

## Components

### Model Classes

#### `EventStatistics`
Holds computed statistics for an event:
- `eventId` - Event identifier
- `eventName` - Event name
- `totalRevenue` - Total revenue from ticket sales
- `totalTicketsSold` - Count of ACTIVE + USED tickets
- `totalCheckedIn` - Count of USED tickets
- `attendanceRate` - Percentage of attendees who checked in

#### `SessionStatistics`
Holds computed statistics for a session:
- `sessionId` - Session identifier
- `sessionTitle` - Session name
- `eventId` - Parent event ID
- `totalTicketsSold` - Tickets sold for this session
- `totalCheckedIn` - Attendees who checked in
- `attendanceRate` - Check-in percentage
- `capacity` - Maximum session capacity
- `capacityUtilization` - Percentage of capacity filled

### Service Interface

`EventStatisticsService` provides the following methods:

```java
// Get comprehensive statistics for an event
Optional<EventStatistics> getEventStatistics(int eventId);

// Calculate revenue for a specific event
double calculateEventRevenue(int eventId);

// Calculate attendance rate for an event
double calculateEventAttendanceRate(int eventId);

// Get statistics for all events
List<EventStatistics> getAllEventStatistics();

// Get statistics for a specific session
Optional<SessionStatistics> getSessionStatistics(int sessionId);

// Get all session statistics for an event
List<SessionStatistics> getEventSessionStatistics(int eventId);

// Find most popular sessions in an event
List<SessionStatistics> getMostPopularSessions(int eventId, int limit);

// Find most popular sessions globally
List<SessionStatistics> getMostPopularSessionsGlobal(int limit);

// Get total tickets sold for an event
int getTotalTicketsSold(int eventId);

// Get total checked-in attendees for an event
int getTotalCheckedIn(int eventId);
```

## Usage Examples

### Initialize the Service

```java
EventDAO eventDAO = new EventDAOImpl();
SessionDAO sessionDAO = new SessionDAOImpl();
TicketDAO ticketDAO = new TicketDAOImpl();

EventStatisticsService statsService = new EventStatisticsServiceImpl(
    eventDAO, sessionDAO, ticketDAO
);
```

### Get Event Statistics

```java
int eventId = 1;
Optional<EventStatistics> stats = statsService.getEventStatistics(eventId);

if (stats.isPresent()) {
    EventStatistics eventStats = stats.get();
    System.out.println("Event: " + eventStats.getEventName());
    System.out.println("Revenue: $" + eventStats.getTotalRevenue());
    System.out.println("Tickets Sold: " + eventStats.getTotalTicketsSold());
    System.out.println("Checked In: " + eventStats.getTotalCheckedIn());
    System.out.println("Attendance Rate: " + eventStats.getAttendanceRate() + "%");
}
```

### Calculate Revenue

```java
double revenue = statsService.calculateEventRevenue(eventId);
System.out.println("Total Revenue: $" + revenue);
```

### Find Most Popular Sessions

```java
// Get top 5 sessions for an event
List<SessionStatistics> topSessions = 
    statsService.getMostPopularSessions(eventId, 5);

for (SessionStatistics session : topSessions) {
    System.out.println(session.getSessionTitle());
    System.out.println("  Tickets Sold: " + session.getTotalTicketsSold());
    System.out.println("  Attendance Rate: " + session.getAttendanceRate() + "%");
    System.out.println("  Capacity: " + session.getCapacityUtilization() + "%");
}
```

### Get All Event Statistics

```java
List<EventStatistics> allStats = statsService.getAllEventStatistics();

double totalRevenue = allStats.stream()
    .mapToDouble(EventStatistics::getTotalRevenue)
    .sum();

System.out.println("Total Revenue Across All Events: $" + totalRevenue);
```

## Implementation Details

### Revenue Calculation
- Sums the `price` field from all tickets for an event
- Filters tickets by:
  - `eventID` matches target event
  - `status` is `ACTIVE` or `USED`

### Attendance Rate Calculation
- **Tickets Sold**: Count of tickets with status `ACTIVE` or `USED`
- **Checked In**: Count of tickets with status `USED`
- **Formula**: `(Checked In / Tickets Sold) × 100`

### Popular Sessions Algorithm
1. Collect statistics for all sessions
2. Sort by `totalTicketsSold` (descending)
3. Return top N sessions based on limit

## Testing

Unit tests are provided in `EventStatisticsServiceTest.java` using JUnit 5 and Mockito:

- Revenue calculation with various ticket statuses
- Attendance rate for full, partial, and zero attendance
- Event statistics retrieval
- Session statistics with capacity utilization
- Most popular sessions ranking
- Edge cases (no tickets, invalid IDs)

Run tests with:
```bash
mvn test -Dtest=EventStatisticsServiceTest
```

## Example Output

```
=== Event Statistics ===
Event: Tech Conference 2025
Total Revenue: $12,500.00
Tickets Sold: 250
Checked In: 187
Attendance Rate: 74.80%

=== Most Popular Sessions (Top 5) ===
1. AI and Machine Learning Workshop
   Tickets Sold: 95
   Checked In: 78
   Attendance Rate: 82.11%
   Capacity Utilization: 95.00%

2. Cloud Architecture Panel
   Tickets Sold: 82
   Checked In: 65
   Attendance Rate: 79.27%
   Capacity Utilization: 82.00%
...
```

## Dependencies

- Java 8+ (for Stream API and LocalDateTime)
- JDBC for database connectivity
- Existing DAO implementations:
  - `EventDAO` / `EventDAOImpl`
  - `SessionDAO` / `SessionDAOImpl`
  - `TicketDAO` / `TicketDAOImpl`

## Notes

- The service assumes numeric IDs for events and sessions
- Non-numeric IDs are skipped with a warning message
- Ticket status values: `ACTIVE`, `USED`, `CANCELLED`
- All calculations handle null and empty collections safely
