# Runnable JAR Instructions

## Building the Runnable JAR

Build the project and create the runnable JAR:

```bash
mvn clean package -DskipTests
```

This creates:
- `target/assignment2-1.0-SNAPSHOT.jar` – runnable shaded JAR with all dependencies
- `target/lib/` – JavaFX runtime JARs for module path execution

## Running the Application

### Windows

**✨ Easiest Method - Double-Click the JAR:**

Simply double-click **`Event-Management-System.jar`** in the project root!

**Alternative Methods:**

1. Double-click `Event Management System.vbs`
2. Run `.\run-app.bat` in PowerShell
3. Manual command:
   ```powershell
   java --module-path "target\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "assignment2-1.0-SNAPSHOT.jar"
   ```

> **Note:** `Event-Management-System.jar` is a small launcher (2 KB) that automatically runs the main application JAR with the correct JavaFX settings.

### macOS / Linux

```bash
java --module-path target/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target/assignment2-1.0-SNAPSHOT.jar
```

## Prerequisites

- Java 17 or later
- PostgreSQL database running
- `.env` file in project root with database credentials:

```
DB_URL=jdbc:postgresql://localhost:5432/your_db
DB_USER=your_user
DB_PASS=your_password
```

## Notes

- The JAR contains all non-JavaFX dependencies (PostgreSQL driver, iText, Gson, etc.)
- JavaFX natives are loaded from `target/lib/` via `--module-path`
- The application initializes the database schema on first launch if tables are missing
- Default login credentials are in `src/main/resources/sql/initial_data.sql`
