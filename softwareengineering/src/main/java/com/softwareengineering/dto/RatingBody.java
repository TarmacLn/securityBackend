package com.softwareengineering.dto;

import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class RatingBody {
    public int appointmentID;
    public int stars;
    public String comments;
    
    public void validate() throws ValidationException {
        if (!InputValidator.isValidID(appointmentID)) {
            throw new ValidationException("Valid appointment ID is required");
        }
        
        if (!InputValidator.isValidRating(stars)) {
            throw new ValidationException("Rating must be between 1 and 5 stars");
        }
        
        if (comments != null && !comments.trim().isEmpty()) {
            if (!InputValidator.isValidText(comments, 1000)) {
                throw new ValidationException("Invalid comments - contains harmful content or exceeds length limit");
            }
            this.comments = InputFilter.sanitizeText(comments);
        } else {
            comments = null;
        }
    }
}
