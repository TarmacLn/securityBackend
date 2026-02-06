package com.softwareengineering.utils;

import java.util.regex.Pattern;

public class InputValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[\\d\\s+()\\-]{7,20}$"
    );
    
    private static final Pattern AMKA_PATTERN = Pattern.compile(
        "^\\d{11}$"
    );
    
    private static final Pattern LICENSE_PATTERN = Pattern.compile(
        "^[A-Z0-9]{1,20}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s'-]{2,100}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('|(\\-\\-)|(;)|(\\|\\|)|(\\*)|(\\*\\*)|(xp_)|(sp_))", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HTML_PATTERN = Pattern.compile(
        "<[^>]*>|&[a-zA-Z]*;|javascript:|on\\w+\\s*=", 
        Pattern.CASE_INSENSITIVE
    );
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches() && email.length() <= 255;
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches() && password.length() <= 128;
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    public static boolean isValidAMKA(String amka) {
        if (amka == null || amka.trim().isEmpty()) {
            return false;
        }
        return AMKA_PATTERN.matcher(amka.trim()).matches();
    }
    
    public static boolean isValidLicenseID(String licenseID) {
        if (licenseID == null || licenseID.trim().isEmpty()) {
            return false;
        }
        return LICENSE_PATTERN.matcher(licenseID.trim()).matches();
    }
    
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(fullName.trim()).matches();
    }
    
    public static boolean isValidText(String text, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return !containsHtmlOrScript(text) && text.length() <= maxLength;
    }
    
    public static boolean isValidOptionalText(String text, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return true;
        }
        return !containsHtmlOrScript(text) && text.length() <= maxLength;
    }
    
    public static boolean isValidID(Integer id) {
        return id != null && id > 0;
    }
    
    public static boolean isValidIntRange(Integer value, int min, int max) {
        return value != null && value >= min && value <= max;
    }
    
    public static boolean isValidRating(int stars) {
        return stars >= 1 && stars <= 5;
    }
    
    public static boolean containsSQLInjection(String text) {
        if (text == null) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(text).find();
    }
    
    public static boolean containsHtmlOrScript(String text) {
        if (text == null) {
            return false;
        }
        return HTML_PATTERN.matcher(text).find();
    }
    
    public static void validateEmailNotNull(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
    }
    
    public static void validatePasswordNotNull(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
    }
}
