# Event Management UI - Pure JavaFX Implementation

## Overview

A comprehensive pure JavaFX user interface for managing events. Built entirely in Java code without FXML or CSS files.

## Features

### 1. **Event List View**
- Table view displaying all events with sortable columns
- Search by event name, location, or ID
- Filter by Event Status (SCHEDULED, ONGOING, COMPLETED, CANCELLED)
- Filter by Event Type (CONFERENCE, WORKSHOP, CONCERT, EXHIBITION)
- Quick action buttons: View, Edit, Delete
- Real-time event count and status updates
- Responsive table layout

### 2. **Event Form View** (Create/Edit)
- Create new events or edit existing ones
- Form fields:
  - Event Name
  - Event Type (dropdown)
  - Location
  - Start Date and Time (with separate pickers and time dropdowns)
  - End Date and Time (with separate pickers and time dropdowns)
  - Duration in days (spinner)
  - Event Status (dropdown)
  - Event Image URL (optional)
  - Session Management (add/remove sessions)
- Form validation with error messages
- Auto-population when editing
- Session list management

### 3. **Event Detail View**
- Full event information display
- Event image viewer (if available)
- Associated sessions list
- Live statistics:
  - Total Revenue
  - Tickets Sold
  - Attendance Rate
- Quick actions: Edit, Delete, Back
- Integrated with EventStatisticsService

## Project Structure

```
src/main/java/group_3/
├── ui/
│   └── EventManagementApp.java          # Main application entry point
├── controller/
│   ├── EventListController.java         # List view logic
│   ├── EventFormController.java         # Create/Edit form logic
│   └── EventDetailController.java       # Detail view logic
└── service/
    └── EventStatisticsService/          # Statistics calculations
```

## Controllers Documentation

### EventListController

**Responsibilities:**
- Manage the main event list view
- Load events from database
- Handle search and filtering
- Manage CRUD operations
- Navigate to detail and form views

**Key Methods:**
```java
EventListController()              // Constructor, initializes UI
void loadEvents()                 // Load events from DAO
void applyFilters()               // Apply search/filter criteria
void handleCreateEvent()           // Open create form
void handleViewEvent(Event)        // Open detail view
void handleEditEvent(Event)        // Open edit form
void handleDeleteEvent(Event)      // Delete with confirmation
void refreshEvents()               // Reload event list
Scene getScene()                   // Return the scene for display
```

**UI Components:**
- TableView with 7 columns (ID, Name, Type, Location, Start Date, Status, Actions)
- TextField for search
- ComboBox filters for Status and Type
- Buttons: Create, Refresh, Search, Clear Filters
- Status bar with event count

### EventFormController

**Responsibilities:**
- Display form for creating or editing events
- Validate form inputs
- Manage session associations
- Save changes to database

**Key Methods:**
```java
EventFormController(Event, EventListController)  // Constructor
void show()                        // Display form in new window
void populateForm()                // Fill form with existing event data
boolean validateForm()             // Validate all inputs
Event buildEvent()                 // Create Event object from form
void handleSave()                  // Validate and save
void handleAddSession()            // Add session to list
void handleRemoveSession()         // Remove selected session
```

**Form Fields:**
- Event ID (read-only for editing)
- Event Name (required)
- Event Type (dropdown, required)
- Location (required)
- Start Date/Time (pickers, required)
- End Date/Time (pickers, required)
- Duration spinner (required)
- Event Status (dropdown, required)
- Image URL (optional)
- Session ID input with add/remove

**Validation Rules:**
- ✅ Required fields enforcement
- ✅ Date range validation (end after start)
- ✅ Duplicate session ID prevention
- ✅ Displays inline error messages

### EventDetailController

**Responsibilities:**
- Display comprehensive event information
- Show event statistics
- Provide event editing and deletion
- Integrate with statistics service

**Key Methods:**
```java
EventDetailController(Event, EventListController)  // Constructor
void show()                        // Display detail view in new window
void displayEventDetails()         // Populate all fields
void loadStatistics()              // Load and display statistics
void handleEdit()                  // Open edit form
void handleDelete()                // Delete with confirmation
void handleViewSession()           // View session details
void handleViewFullStatistics()    // Show detailed statistics dialog
```

**Displayed Information:**
- Event image (if available)
- Basic info: ID, Name, Type, Status
- Schedule: Start/End dates, Duration, Location
- Associated sessions
- Statistics: Revenue, Tickets Sold, Attendance Rate

## Running the Application

### Prerequisites
- Java 17+
- JavaFX 21.0.2 (already in pom.xml)
- PostgreSQL database configured

### Option 1: Maven
```bash
mvn clean javafx:run
```

### Option 2: Command Line
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="group_3.ui.EventManagementApp"
```

### Option 3: IDE
1. Open EventManagementApp.java
2. Run as Java Application

## User Guide

### Creating an Event

1. Click **"+ Create Event"** button
2. Fill in required fields (marked with *)
3. Select dates using DatePicker
4. Choose times from hour/minute dropdowns
5. Optionally add sessions by entering IDs
6. Click **"Save Event"**

### Viewing Event Details

1. In the event list, click **"View"** button
2. Review comprehensive information
3. Check associated sessions and statistics
4. Use Edit or Delete buttons as needed

### Editing an Event

1. Click **"Edit"** button in list or detail view
2. Form auto-populates with existing data
3. Modify any fields
4. Click **"Save Event"**

### Searching Events

1. Type in the search box (searches name, location, ID)
2. Press Enter or click "Search"
3. Results update in real-time

### Filtering Events

1. Select status from "Status:" dropdown
2. Select type from "Type:" dropdown
3. Combine with search for advanced filtering
4. Click "Clear Filters" to reset

### Deleting Events

1. Click **"Delete"** button
2. Confirm in dialog box
3. Event is permanently removed
4. List automatically refreshes

## UI Design

### Color Scheme
- **Primary Blue**: #3498db (main actions, headers)
- **Secondary Gray**: #95a5a6 (secondary actions)
- **Danger Red**: #e74c3c (delete operations)
- **Success Green**: #27ae60 (completed status)
- **Warning Orange**: #f39c12 (ongoing status)
- **Background**: #f5f5f5 / #f8f9fa (light backgrounds)

### Styling Approach
- Inline CSS styling using `setStyle()` method
- Consistent button styling across all views
- Color-coded status badges
- Hover effects on buttons and table rows
- Responsive layout using JavaFX layout managers

### Layout Managers Used
- **BorderPane**: Main container structure
- **HBox**: Horizontal layouts (headers, button bars)
- **VBox**: Vertical layouts (forms, sections)
- **GridPane**: Table-like layouts (statistics, details)
- **ScrollPane**: Scrollable content areas

## Integration Points

### Backend Integration
- **EventDAO**: CRUD operations for events
- **SessionDAO**: Session data retrieval
- **TicketDAO**: Ticket data for statistics
- **EventStatisticsService**: Real-time statistics calculation

### Data Flow
1. UI loads events from EventDAO
2. User actions trigger form or detail views
3. Form saves to EventDAO
4. Detail view calculates statistics via EventStatisticsService
5. UI refreshes automatically after operations

## Features Implemented

✅ **Fully Implemented:**
- Event list view with table
- Search and filter functionality
- Create new events
- Edit existing events
- Delete events with confirmation
- View event details
- Event statistics display
- Form validation
- Session management
- Image display (with fallback)
- Status-based styling

⏳ **Not Yet Implemented:**
- Session detail view
- Session editing
- Bulk operations
- Event export (CSV/PDF)
- Calendar view
- Real-time notifications

## Key Design Patterns

### Model-View-Controller (MVC)
- **Model**: Event, EventStatistics classes
- **View**: JavaFX Scene creation in controllers
- **Controller**: EventListController, EventFormController, EventDetailController

### Observer Pattern
- ObservableList for dynamic table updates
- Event listeners on buttons and form fields

### Separation of Concerns
- UI building separate from business logic
- DAO layer handles database operations
- Service layer handles calculations

## Error Handling

All operations include robust error handling:
- Database connection errors
- Invalid input validation
- Resource loading failures
- Graceful error dialogs

Example:
```java
try {
    List<Event> events = eventDAO.findAll();
    eventList.setAll(events);
} catch (Exception e) {
    showError("Error loading events", e.getMessage());
}
```

## Performance Considerations

- **Lazy Loading**: Statistics only calculated when needed
- **Efficient Filtering**: Uses stream API for filtering
- **Smart Caching**: ObservableList caches events
- **Minimal Redraws**: Only update UI when necessary

## Testing

To test the application:

1. **List View**:
   - Create several events with different statuses/types
   - Test search with various keywords
   - Test filters individually and combined
   - Verify action buttons work

2. **Form Validation**:
   - Try saving with empty required fields
   - Test date validation (end before start)
   - Add duplicate sessions

3. **Detail View**:
   - Check all information displays correctly
   - Verify statistics load
   - Test edit and delete operations

4. **Database Integration**:
   - Verify changes persist after close/reopen
   - Check cascading deletes if applicable

## Troubleshooting

### Issue: Window doesn't display
**Solution**: Ensure EventManagementApp.main() is called

### Issue: Events don't load
**Solution**: Verify database connection and EventDAO implementation

### Issue: Statistics show N/A
**Solution**: Ensure EventStatisticsService is configured correctly

### Issue: Date pickers don't work
**Solution**: Ensure Java 17+ is being used

## Future Enhancements

Potential improvements:
- [ ] Session detail and editing views
- [ ] Advanced search with date range
- [ ] Event duplication
- [ ] Drag-and-drop functionality
- [ ] Dark mode toggle
- [ ] Print event details
- [ ] Export to iCal format
- [ ] Real-time search suggestions

## Code Quality

- **No FXML/CSS Dependencies**: Pure Java implementation
- **Well-Documented**: Comprehensive javadoc comments
- **DRY Principle**: Helper methods reduce duplication
- **Clean Code**: Clear naming and structure
- **Error Handling**: Try-catch with user-friendly messages

## Support

For issues or questions:
1. Check controller javadoc comments
2. Review UI component initialization in createScene()
3. Verify database connection
4. Check backend service configuration
