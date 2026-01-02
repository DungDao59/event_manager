module assignment2 {
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.google.gson;
    exports group_3;
    exports group_3.ui;
    exports group_3.controller;
    exports group_3.model;
    exports group_3.model.enums;
    exports group_3.dao;
    exports group_3.dao.impl;
    exports group_3.service.AttendeeService;
    exports group_3.service.AuthService;
    exports group_3.service.EventAdminService;
    exports group_3.service.EventStatisticsService;
    exports group_3.service.PresenterService;
    exports group_3.service.RegistrationService;
    exports group_3.service.ScheduleService;
    exports group_3.service.SystemHistoryService;
    exports group_3.service.UserService;
    exports group_3.util;
    opens group_3.controller to javafx.fxml;
    opens group_3.model to javafx.base;
}