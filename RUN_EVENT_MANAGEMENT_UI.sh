#!/bin/bash

# Event Management System - Quick Start Guide
# Pure JavaFX Implementation (No FXML/CSS)

echo "==================================="
echo "Event Management System"
echo "Quick Start"
echo "==================================="
echo ""

# Check Java version
echo "Checking Java version..."
java -version 2>&1 | head -1
echo ""

# Compile and run
echo "Building and launching application..."
echo ""

mvn clean compile javafx:run

echo ""
echo "Application launched!"
