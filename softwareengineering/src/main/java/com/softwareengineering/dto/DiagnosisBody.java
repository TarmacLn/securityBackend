package com.softwareengineering.dto;

import com.softwareengineering.models.Diagnosis;
import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class DiagnosisBody {
    public Integer appointmentID;
    public String decease;
    public String details;

    public DiagnosisBody() {
    }

    public DiagnosisBody(Diagnosis diagnosis) {
        this.appointmentID = diagnosis.getInteger("appointmentID");
        this.decease = diagnosis.getString("decease");
        this.details = diagnosis.getString("details");
    }
    
    public void validate() throws ValidationException {
        if (appointmentID == null || !InputValidator.isValidID(appointmentID)) {
            throw new ValidationException("Valid appointment ID is required");
        }
        
        if (decease == null || decease.trim().isEmpty()) {
            throw new ValidationException("Disease/condition is required");
        }
        
        if (!InputValidator.isValidText(decease, 200)) {
            throw new ValidationException("Invalid disease name - contains harmful content or exceeds length limit");
        }
        
        if (details != null && !details.trim().isEmpty()) {
            if (!InputValidator.isValidText(details, 2000)) {
                throw new ValidationException("Invalid details - contains harmful content or exceeds length limit");
            }
            this.details = InputFilter.sanitizeText(details);
        } else {
            details = null;
        }
        
        this.decease = InputFilter.sanitizeText(decease);
    }
}