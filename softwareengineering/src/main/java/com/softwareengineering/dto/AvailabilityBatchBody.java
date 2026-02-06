package com.softwareengineering.dto;

import java.sql.Timestamp;
import java.util.List;

import com.softwareengineering.utils.ValidationException;

public class AvailabilityBatchBody {
    public List<Timestamp> slots;
    
    public void validate() throws ValidationException {
        if (slots == null || slots.isEmpty()) {
            throw new ValidationException("At least one availability slot is required");
        }
        
        if (slots.size() > 100) {
            throw new ValidationException("Cannot create more than 100 slots at once");
        }
        
        long now = System.currentTimeMillis();
        
        for (Timestamp slot : slots) {
            if (slot == null) {
                throw new ValidationException("Slot cannot be null");
            }
            
            if (slot.getTime() < now) {
                throw new ValidationException("Slot date cannot be in the past");
            }
        }
    }
}
