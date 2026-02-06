package com.softwareengineering.dto;

import com.softwareengineering.models.enums.City;
import com.softwareengineering.models.enums.Speciality;
import com.softwareengineering.models.enums.UserTypeEnum;
import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class RegisterBody {
    public UserTypeEnum userType;
    public String fullName;
    public String email;
    public String password;
    public String phone;
    public String amka;
    public String licenceID;
    public Speciality speciality;
    public City officeLocation;
    public String bio;
    public Boolean isDark;
    
    public void validate() throws ValidationException {
        if (userType == null) {
            throw new ValidationException("User type is required");
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        if (!InputValidator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        if (!InputValidator.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
        }
        
        if (!InputValidator.isValidFullName(fullName)) {
            throw new ValidationException("Invalid full name format");
        }
        
        if (phone != null && !phone.trim().isEmpty() && !InputValidator.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format");
        }
        
        // Doctor
        if (userType == UserTypeEnum.DOCTOR) {
            if (licenceID == null || licenceID.trim().isEmpty()) {
                throw new ValidationException("License ID is required for doctors");
            }
            
            if (!InputValidator.isValidLicenseID(licenceID)) {
                throw new ValidationException("Invalid license ID format");
            }
            
            if (speciality == null) {
                throw new ValidationException("Speciality is required for doctors");
            }
            
            if (officeLocation == null) {
                throw new ValidationException("Office location is required for doctors");
            }
        }
        
        // Patient
        if (userType == UserTypeEnum.PATIENT) {
            if (amka == null || amka.trim().isEmpty()) {
                throw new ValidationException("AMKA is required for patients");
            }
            
            if (!InputValidator.isValidAMKA(amka)) {
                throw new ValidationException("Invalid AMKA format");
            }
        }
        
        if (bio != null && !bio.trim().isEmpty() && 
            !InputValidator.isValidOptionalText(bio, 500)) {
            throw new ValidationException("Invalid bio - contains harmful content");
        }
        
        this.email = InputFilter.sanitizeEmail(email);
        this.fullName = InputFilter.sanitizeName(fullName);
        this.phone = InputFilter.sanitizePhone(phone);
        this.amka = InputFilter.sanitizeAMKA(amka);
        this.licenceID = InputFilter.sanitizeLicenseID(licenceID);
        this.bio = InputFilter.sanitizeText(bio);
    }
    
    public void validateForUpdate() throws ValidationException {
        if (fullName != null && !fullName.trim().isEmpty()) {
            if (!InputValidator.isValidFullName(fullName)) {
                throw new ValidationException("Invalid full name format");
            }
        }
        
        if (email != null && !email.trim().isEmpty()) {
            if (!InputValidator.isValidEmail(email)) {
                throw new ValidationException("Invalid email format");
            }
        }
        
        if (password != null && !password.trim().isEmpty()) {
            if (!InputValidator.isValidPassword(password)) {
                throw new ValidationException("Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
            }
        }
        
        if (phone != null && !phone.trim().isEmpty() && !InputValidator.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format");
        }
        
        if (userType != null && userType == UserTypeEnum.DOCTOR) {
            if (licenceID != null && !licenceID.trim().isEmpty()) {
                if (!InputValidator.isValidLicenseID(licenceID)) {
                    throw new ValidationException("Invalid license ID format");
                }
            }
        }
        
        if (userType != null && userType == UserTypeEnum.PATIENT) {
            if (amka != null && !amka.trim().isEmpty()) {
                if (!InputValidator.isValidAMKA(amka)) {
                    throw new ValidationException("Invalid AMKA format");
                }
            }
        }
        
        if (bio != null && !bio.trim().isEmpty() && 
            !InputValidator.isValidOptionalText(bio, 500)) {
            throw new ValidationException("Invalid bio - contains harmful content");
        }
        
        if (email != null && !email.trim().isEmpty()) {
            this.email = InputFilter.sanitizeEmail(email);
        }
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = InputFilter.sanitizeName(fullName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = InputFilter.sanitizePhone(phone);
        }
        if (amka != null && !amka.trim().isEmpty()) {
            this.amka = InputFilter.sanitizeAMKA(amka);
        }
        if (licenceID != null && !licenceID.trim().isEmpty()) {
            this.licenceID = InputFilter.sanitizeLicenseID(licenceID);
        }
        if (bio != null && !bio.trim().isEmpty()) {
            this.bio = InputFilter.sanitizeText(bio);
        }
    }
}
