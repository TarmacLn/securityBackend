package com.softwareengineering.dto;

import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

public class SummaryBody {
    public String prompt;

    public SummaryBody() {}

    public SummaryBody(String prompt) {
        this.prompt = prompt;
    }
    
    public void validate() throws ValidationException {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new ValidationException("Prompt is required");
        }
        
        if (!InputValidator.isValidText(prompt, 5000)) {
            throw new ValidationException("Invalid prompt - contains harmful content or exceeds length limit");
        }
        
        this.prompt = InputFilter.sanitizeText(prompt);
    }
}
