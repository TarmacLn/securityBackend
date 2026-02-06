package com.softwareengineering.dto;

import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class LoginBody {
    public String email;
    public String password;
    
    public void validate() throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        if (!InputValidator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        this.email = InputFilter.sanitizeEmail(email);
    }
}
