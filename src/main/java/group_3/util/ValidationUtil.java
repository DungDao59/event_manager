package group_3.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.*;

/**
 * @author Group 3
 *
 * Utility class for input validation, including null checks,
 * blank checks, and pattern-based validation.
 */


public class ValidationUtil {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private ValidationUtil(){};

    public static void validateNotNull(Object obj, String message){
        if(obj == null ){
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(String value, String fieldName){
        if(value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    public static void validatePositiveId(int id, String fieldName){
        if(id <= 0){
            throw new IllegalArgumentException(fieldName + " must be positive number");
        }
    }

    /* =======================
      AUTH / USER VALIDATION
      ======================= */
    public static void validateUsername(String username){
        validateNotNull(username, "Username cannot be null");
        if(!USERNAME_PATTERN.matcher(username).matches()){
            throw new IllegalArgumentException(
                    "Username must be 4-20 characters and contain only letters, numbers, and underscores"
            );
        }
    }

    public static void validatePassword(String password){
        validateNotNull(password,"Password can't be null");
        if(!PASSWORD_PATTERN.matcher(password).matches()){
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters contain letters and numbers"
            );
        }
    }

    public static void validateDateOfBirth(LocalDate dob){
        validateNotNull(dob,"Date of birth cannot be null");
        if(dob.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
    }

    /* =======================
       EVENT & SESSION VALIDATION
       ======================= */
    public static void validateDateRange(LocalDateTime startTime, LocalDateTime endTime){
        validateNotNull(startTime, "Start date cannot be null");
        validateNotNull(endTime, "End date cannot be null");

        if(startTime.isBefore(endTime)){
            throw new IllegalArgumentException(
                    "Start date must be before end date"
            );
        }
    }

    public static void validateEventDuration(LocalDateTime start, LocalDateTime end){
        validateDateRange(start, end);

        if(start.plusYears(1).isBefore(end)){
            throw new IllegalArgumentException("Event duration is unrealistically long");
        }
    }

    public static void validateLocation(String location){
        notBlank(location,"Location");
        if(location.length() > 255){
            throw new IllegalArgumentException("Location is too long");
        }
    }

    /* =======================
       TICKET & REGISTRATION
       ======================= */

    public static void validatePrice(double price){
        if(price < 0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    /* =======================
       TICKET & REGISTRATION
       ======================= */
    public static void validateReportType(String reportType){
        notBlank(reportType,"Report type");
    }

}
