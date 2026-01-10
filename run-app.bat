@echo off
REM Event Management System - Launcher Script
REM Run this script from the project root directory

echo Starting Event Management System...
echo.

java --module-path "target\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar "assignment2-1.0-SNAPSHOT.jar"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error: Failed to start the application.
    echo Please ensure:
    echo 1. Java 17 or later is installed
    echo 2. You have built the project with: mvn clean package -DskipTests
    echo 3. PostgreSQL is running and .env file is configured
    pause
)
