package com.softwareengineering.dto;

import java.sql.Timestamp;

import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class AvailabilityBody {
    public Timestamp date;
    public Timestamp timeFrom;
    public Integer doctorID;
    public Boolean free;
    
    public void validate() throws ValidationException {
        if (date == null) {
            throw new ValidationException("Date is required");
        }
        
        if (timeFrom == null) {
            throw new ValidationException("Start time is required");
        }
        
        if (doctorID == null || !InputValidator.isValidID(doctorID)) {
            throw new ValidationException("Valid doctor ID is required");
        }
        
        // Date validation
        long now = System.currentTimeMillis();
        if (date.getTime() < now) {
            throw new ValidationException("Availability date cannot be in the past");
        }
        
        if (timeFrom.getTime() < now) {
            throw new ValidationException("Availability time cannot be in the past");
        }
        
        if (timeFrom.getTime() < date.getTime()) {
            throw new ValidationException("Start time must be on or after the availability date");
        }
    }
}
