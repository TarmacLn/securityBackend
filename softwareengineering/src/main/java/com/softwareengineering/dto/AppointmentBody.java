package com.softwareengineering.dto;

import com.softwareengineering.models.enums.Status;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class AppointmentBody {
    public Integer appointmentID;
    public Integer doctorID;
    public Integer patientID;
    public Status status;
    public Integer slotID;
    public String reason;
    
    public void validate() throws ValidationException {
        if (doctorID == null || !InputValidator.isValidID(doctorID)) {
            throw new ValidationException("Valid doctor ID is required");
        }
        
        if (slotID == null || !InputValidator.isValidID(slotID)) {
            throw new ValidationException("Valid slot ID is required");
        }
        
        if (reason != null && !reason.trim().isEmpty()) {
            if (!InputValidator.isValidText(reason, 500)) {
                throw new ValidationException("Invalid reason - contains harmful content or exceeds length limit");
            }
        } else {
            reason = null;
        }
    }
    
    public void validateForCancel() throws ValidationException {
        if (appointmentID == null || !InputValidator.isValidID(appointmentID)) {
            throw new ValidationException("Valid appointment ID is required");
        }
    }
    
    public void validateForComplete() throws ValidationException {
        if (appointmentID == null || !InputValidator.isValidID(appointmentID)) {
            throw new ValidationException("Valid appointment ID is required");
        }
    }
}
