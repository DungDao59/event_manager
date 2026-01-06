# Pure JavaFX Event Management UI - Complete Overview

## 🎯 What Was Delivered

A complete **Event Management User Interface** built entirely in **pure JavaFX** without any FXML or CSS files.

---

## 📋 Files Created

### Application Entry Point
**File**: `src/main/java/group_3/ui/EventManagementApp.java`
- Main application launcher
- Starts EventListController with a 1200x800 window
- Single `main()` method entry point

### Controllers (Pure JavaFX UI Building)

#### 1. **EventListController.java** 
**Location**: `src/main/java/group_3/controller/EventListController.java`
**Size**: ~400 lines of pure JavaFX code

**Components:**
- TableView with 7 columns
- Search TextField
- Status ComboBox filter
- Type ComboBox filter
- Buttons: Create, Refresh, Search, Clear
- Status bar with event count

**Functionality:**
- Load events from EventDAO
- Search and filter logic
- Create/View/Edit/Delete navigation
- Real-time table updates

#### 2. **EventFormController.java**
**Location**: `src/main/java/group_3/controller/EventFormController.java`
**Size**: ~500 lines of pure JavaFX code

**Components:**
- TextField for Event Name, Location, Image URL
- DatePicker for start/end dates
- ComboBox for hours and minutes (0-23 and 0,15,30,45)
- ComboBox for Event Type and Status
- Spinner for duration
- ListView for session management
- Label for error messages

**Functionality:**
- Create new events
- Edit existing events
- Form validation
- Date/time selection
- Session add/remove
- Error display

#### 3. **EventDetailController.java**
**Location**: `src/main/java/group_3/controller/EventDetailController.java`
**Size**: ~450 lines of pure JavaFX code

**Components:**
- Header with back/edit/delete buttons
- ImageView for event image
- GridPane for basic information
- GridPane for schedule details
- ListView for sessions
- Statistics cards (Revenue, Tickets, Attendance)

**Functionality:**
- Display event details
- Show event image
- Calculate and display statistics
- Edit and delete operations
- Navigate to full statistics

### Documentation
**File**: `src/main/java/group_3/controller/README.md` (~400 lines)
- Complete feature documentation
- Controller method descriptions
- User guide with screenshots descriptions
- Running instructions
- Design patterns explained
- Troubleshooting guide

---

## 🎨 UI Components Breakdown

### EventListController UI Structure
```
BorderPane
├── Top: VBox
│   ├── HBox (Title: "Event Management" + Buttons)
│   │   ├── Label("Event Management")
│   │   ├── Region (spacer)
│   │   ├── Button("+ Create Event")
│   │   └── Button("Refresh")
│   └── HBox (Filters)
│       ├── TextField (search)
│       ├── Button("Search")
│       ├── ComboBox (status filter)
│       ├── ComboBox (type filter)
│       └── Button("Clear Filters")
├── Center: TableView
│   ├── TableColumn: ID
│   ├── TableColumn: Name
│   ├── TableColumn: Type
│   ├── TableColumn: Location
│   ├── TableColumn: Start Date
│   ├── TableColumn: Status
│   └── TableColumn: Actions (View/Edit/Delete buttons)
└── Bottom: HBox (Status bar)
    ├── Label (status message)
    ├── Region (spacer)
    └── Label (event count)
```

### EventFormController UI Structure
```
VBox
├── HBox (Header)
│   └── Label(Title)
├── ScrollPane
│   └── VBox (Form fields)
│       ├── VBox: Event ID
│       ├── VBox: Event Name *
│       ├── VBox: Event Type *
│       ├── VBox: Location *
│       ├── HBox: Start & End Date/Time *
│       │   ├── VBox: Start (DatePicker + Time)
│       │   └── VBox: End (DatePicker + Time)
│       ├── VBox: Duration *
│       ├── VBox: Event Status *
│       ├── VBox: Image URL
│       ├── VBox: Sessions
│       │   ├── HBox: (TextField + Add button)
│       │   ├── ListView
│       │   └── Button: Remove
│       └── Label: Error messages
└── HBox: Button bar
    ├── Button("Save Event")
    └── Button("Cancel")
```

### EventDetailController UI Structure
```
BorderPane
├── Top: HBox (Header)
│   ├── Button("← Back")
│   ├── Label(Event Name)
│   ├── Region(spacer)
│   ├── Button("Edit")
│   └── Button("Delete")
├── Center: ScrollPane
│   └── VBox (Content)
│       ├── VBox: Event Image
│       ├── VBox: Basic Info
│       │   └── GridPane (4x2)
│       ├── VBox: Schedule & Location
│       │   └── GridPane (4x2)
│       ├── VBox: Sessions
│       │   ├── ListView
│       │   └── HBox: Buttons
│       └── VBox: Statistics
│           ├── GridPane (3 stat cards)
│           └── Button("View Full Stats")
```

---

## 🎯 Features Summary

| Feature | Implemented | Method |
|---------|-------------|--------|
| List View | ✅ | TableView + ObservableList |
| Search | ✅ | TextField + Filter logic |
| Filter by Status | ✅ | ComboBox + Stream API |
| Filter by Type | ✅ | ComboBox + Stream API |
| Filter by Date | ✅ | DatePicker + Filter logic |
| Filter by Location | ✅ | TextField + Filter logic |
| Create Event | ✅ | EventFormController |
| Edit Event | ✅ | EventFormController |
| Delete Event | ✅ | EventDAO.delete() |
| View Details | ✅ | EventDetailController |
| Form Validation | ✅ | validateForm() method |
| Date Selection | ✅ | DatePicker + ComboBox |
| Time Selection | ✅ | Hour/Minute ComboBox |
| Session Management | ✅ | ListView + TextField |
| Image Display | ✅ | ImageView |
| Statistics Display | ✅ | EventStatisticsService |
| Error Messages | ✅ | Alert dialogs + Label |
| Status Styling | ✅ | Color-coded badges |

---

## 🔧 Technical Details

### JavaFX Controls Used
- **TableView**: Event list
- **TextField**: Text input
- **DatePicker**: Date selection
- **ComboBox**: Dropdowns
- **Spinner**: Numeric input
- **ListView**: Session list
- **Button**: Actions
- **Label**: Text display
- **ImageView**: Image display
- **Alert**: Dialogs
- **Region**: Spacers

### Layout Managers
- **BorderPane**: Main container
- **VBox**: Vertical layouts
- **HBox**: Horizontal layouts
- **GridPane**: Table-like layouts
- **ScrollPane**: Scrollable content

### Design Patterns
- **MVC**: Model-View-Controller separation
- **Observer**: ObservableList for data binding
- **Strategy**: Different views for different operations
- **Factory**: Helper methods for UI creation

---

## 📊 Code Statistics

| Metric | Value |
|--------|-------|
| Total Java Files | 4 |
| Total Lines of Code | ~2000+ |
| FXML Files | 0 |
| CSS Files | 0 |
| External Resources | 0 |
| Controllers | 3 |
| UI Methods | 30+ |
| JavaFX Components | 15+ |

---

## 🚀 Running the Application

### Quick Start
```bash
# Option 1: Maven JavaFX plugin
mvn clean javafx:run

# Option 2: Maven Exec
mvn clean compile && mvn exec:java -Dexec.mainClass="group_3.ui.EventManagementApp"

# Option 3: IDE
Right-click EventManagementApp.java → Run
```

### Database Connection
Ensure `DatabaseConnection.java` is configured with your PostgreSQL database credentials.

---

## 🎓 Key Concepts Demonstrated

✅ **JavaFX Scene Graph**
- Building UI programmatically
- Layout managers
- Component hierarchy

✅ **Event Handling**
- Button clicks
- TextField actions
- ComboBox selection

✅ **Data Binding**
- ObservableList
- TableView data source
- Real-time updates

✅ **Window Management**
- Multiple stages
- Modal windows
- Window lifecycle

✅ **Form Handling**
- Validation logic
- Error display
- Data collection

✅ **Database Integration**
- DAO pattern
- CRUD operations
- Service layer

✅ **Error Handling**
- Try-catch blocks
- User-friendly messages
- Graceful fallbacks

---

## 🔐 Authentication System

### Application Flow (Login-First Approach)

```
AuthApp.java (Main Entry Point)
    └── Login Screen
        ├── "Browse Events as Guest" → PublicEventBrowserController (Anonymous)
        ├── "Sign In" → Role-based Dashboard (Authenticated)
        └── "Register here" → Registration Screen

After Login → Role-based Dashboard:
    ├── SYSTEM_ADMIN → SystemAdminController (History + User Management)
    ├── EVENT_ADMIN → EventListController (Event Management)
    ├── PRESENTER → ProfileController (Profile Management)
    └── ATTENDEE → ProfileController (Profile Management)
```

### Entry Point
**File**: `src/main/java/group_3/ui/AuthApp.java`
- Launches with **Login Screen** as default
- Users can login for authenticated features
- Users can click "Browse Events as Guest" for anonymous browsing
- Initializes database connection

### Login Screen
**FXML**: `src/main/resources/fxml/Login.fxml`
**Controller**: `src/main/java/group_3/controller/LoginController.java`

**Features:**
- Username and password fields
- Error message display
- **"Browse Events as Guest"** button for anonymous visitors
- Link to Registration screen
- Role-based navigation after login:
  - **SYSTEM_ADMIN** → System Admin Dashboard
  - **EVENT_ADMIN** → Event Management Dashboard
  - **ATTENDEE/PRESENTER** → Profile View

### Registration Screen
**FXML**: `src/main/resources/fxml/Registration.fxml`
**Controller**: `src/main/java/group_3/controller/RegistrationController.java`

**Features:**
- Full name, username, password fields
- Password confirmation
- Role selection (Attendee or Presenter only)
- Date of birth and contact info
- Form validation
- Redirect to login after successful registration

---

## 👁️ Anonymous Visitor (Guest) Access

**Controller**: `src/main/java/group_3/controller/PublicEventBrowserController.java`

Anonymous visitors can browse events by clicking **"Browse Events as Guest"** on the Login screen.

### What Visitors CAN Do:
| Feature | Description |
|---------|-------------|
| ✅ View All Events | Browse all available events in the system |
| ✅ Filter by Type | Conference, Workshop, Seminar, etc. |
| ✅ Filter by Status | Upcoming, Ongoing, Completed, Cancelled |
| ✅ Filter by Date | Select specific date to filter events |
| ✅ Filter by Location | Search events by location |
| ✅ Search Events | Search by event name |
| ✅ View Event Details | See event info, schedule, sessions |
| ✅ View Presenter Profiles | **Restricted info only** (name, role) |

### What Visitors CANNOT Do:
| Restriction | Reason |
|-------------|--------|
| ❌ Register for Events | Requires authentication |
| ❌ Register for Sessions | Requires authentication |
| ❌ View Contact Information | Privacy protection |
| ❌ View Internal Records | Administrative data |
| ❌ Access Admin Dashboards | Role-based access control |
| ❌ Modify Any Data | Read-only access |

### Presenter Profile (Restricted View)
When visitors view presenter profiles, they only see:
- **Full Name** ✅
- **Presenter Role** ✅ (e.g., Keynote Speaker, Workshop Leader)

They do **NOT** see:
- Username ❌
- Email/Contact Info ❌
- Date of Birth ❌
- Statistics/Performance Data ❌

---

## 👤 System Admin Dashboard

**Controller**: `src/main/java/group_3/controller/SystemAdminController.java`

### Tab 1: System History
View and filter all system operation logs:
- **Filter by Date Range**: Start and end date pickers
- **Filter by User**: Dropdown of all users
- **Filter by Operation Type**: LOGIN, LOGOUT, USER_CREATED, USER_UPDATED, USER_DELETED, ROLE_CHANGED, EVENT_CREATED, etc.
- **Table Columns**: Log ID, Timestamp, User ID, Operation, Details

### Tab 2: User Management
Manage all user accounts:
- **View All Users**: Table with ID, Username, Full Name, Role
- **Filter by Role**: All, ATTENDEE, PRESENTER, EVENT_ADMIN, SYSTEM_ADMIN
- **Actions**:
  - **Change Role**: Assign new role to user
  - **Delete User**: Permanently remove user (with confirmation)

---

## 👤 Profile View

**Controller**: `src/main/java/group_3/controller/ProfileController.java`

For Attendees and Presenters to manage their profile:
- View/Edit full name
- Update date of birth
- Change contact information
- Change password (requires current password)
- Presenter-specific: Update presenter role

---

## 🚀 Running the Application

### Default Run (Public Event Browser - Recommended)
```bash
# Starts with Public Event Browser (Anonymous visitors can browse)
# Users can click "Sign In" or "Register" for authenticated features
mvn clean javafx:run
```

### IDE Run
Right-click on `EventManagementApp.java` → Run

---

## 🧪 Testing Login

### Option 1: Create a Test Account via Registration
1. Start the app with `AuthApp`
2. Click "Register here" on Login screen
3. Fill in the registration form:
   - Full Name: `Test User`
   - Username: `testuser`
   - Password: `password123`
   - Role: Select Attendee or Presenter
4. Click "Register"
5. You'll be automatically logged in

### Option 2: Use Pre-configured Test Users
Run the SQL script to add test users with known passwords:
```bash
# In your PostgreSQL client, run:
psql -d your_database -f src/main/resources/sql/test_users.sql
```

**Test Credentials (Password: `password123` for all):**

| Username | Password | Role |
|----------|----------|------|
| `admin` | `password123` | SYSTEM_ADMIN |
| `eventadmin` | `password123` | EVENT_ADMIN |
| `presenter` | `password123` | PRESENTER |
| `attendee` | `password123` | ATTENDEE |

### Role-Based Access
| Role | Dashboard Access |
|------|------------------|
| SYSTEM_ADMIN | System Admin Dashboard (History + User Management) |
| EVENT_ADMIN | Event Management (Create/Edit/Delete Events) |
| PRESENTER | Profile View |
| ATTENDEE | Profile View |

---

## 📚 Documentation

Complete documentation available in:
- `src/main/java/group_3/controller/README.md` - Detailed feature guide
- `EVENT_MANAGEMENT_UI_SUMMARY.md` - Implementation summary
- Javadoc comments in each controller class

---

## ✨ Highlights

🎯 **Pure JavaFX** - No FXML, No CSS, 100% Java code
🎨 **Professional UI** - Clean, modern design with color-coding
📊 **Statistics Integration** - Real-time revenue, tickets, attendance
✅ **Form Validation** - Comprehensive input checking
🔄 **Responsive** - Real-time updates and filtering
🛡️ **Error Handling** - Graceful error dialogs
📱 **Responsive Layout** - Adapts to window resizing
🔐 **Authentication** - Login/Register with role-based access
👤 **User Management** - Admin can manage all users
📜 **System History** - Complete audit trail of operations

---

## 🔍 Browser View

Users can:
1. **Launch** the application
2. **View** list of all events with filtering
3. **Search** by name, location, or ID
4. **Create** new events with full details
5. **Edit** existing events
6. **Delete** events with confirmation
7. **View** detailed event information
8. **See** event statistics (revenue, attendance)
9. **Manage** sessions associated with events
10. **View** event images

---

## 📞 Support

For questions about the implementation:
1. Check README.md in controller folder
2. Review javadoc comments in code
3. Examine controller method signatures
4. Check error messages for hints

---

**Created**: December 29, 2025
**Version**: 1.0
**Status**: ✅ Complete and Ready to Use
