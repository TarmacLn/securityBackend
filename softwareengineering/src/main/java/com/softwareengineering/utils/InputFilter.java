package com.softwareengineering.utils;

public class InputFilter {
    
    public static String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        
        return input
            .trim()
            .replaceAll("<[^>]*>", "")// HTML tags
            .replaceAll("javascript:", "")// javascript
            .replaceAll("on\\w+\\s*=", "")// event handlers
            .replaceAll("&[a-zA-Z]+;", "")// HTML entities
            .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");// control characters
    }
    
    public static String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
    
    public static String sanitizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.trim()
            .replaceAll("[^\\d\\s+()\\-]", "");
    }
    
    public static String sanitizeName(String name) {
        if (name == null) {
            return null;
        }
        return name.trim()
            .replaceAll("[^a-zA-Z\\s'-]", "")
            .replaceAll("\\s+", " ");// Collapse multiple spaces
    }
    
    public static String sanitizeAMKA(String amka) {
        if (amka == null) {
            return null;
        }
        return amka.trim().replaceAll("\\D", "");// only digits
    }
    
    public static String sanitizeLicenseID(String licenseID) {
        if (licenseID == null) {
            return null;
        }
        return licenseID.trim()
            .toUpperCase()
            .replaceAll("[^A-Z0-9]", "");
    }
    
    public static String sanitizeText(String text) {
        if (text == null) {
            return null;
        }
        return sanitizeString(text);
    }
    
    public static String sanitizeOptionalText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return sanitizeString(text);
    }
    
    public static String sanitizeAvatar(String avatar) {
        if (avatar == null) {
            return null;
        }
        String sanitized = avatar.trim();
        
        // Validate it's either a valid URL or base64
        if (sanitized.startsWith("http://") || sanitized.startsWith("https://")) {
            // no javascript: allowed
            if (sanitized.contains("javascript:")) {
                throw new ValidationException("Invalid avatar URL");
            }
            return sanitized;
        } else if (isValidBase64(sanitized)) {
            return sanitized;
        } else {
            throw new ValidationException("Avatar must be a valid URL or base64 encoded image");
        }
    }
    
    private static boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("^[A-Za-z0-9+/]*={0,2}$") && str.length() % 4 == 0;
    }
    
    public static String truncateString(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (input.length() > maxLength) {
            return input.substring(0, maxLength);
        }
        return input;
    }
    
    public static String escapeForDatabase(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "''")
                   .replace("\"", "\"\"");
    }
}
