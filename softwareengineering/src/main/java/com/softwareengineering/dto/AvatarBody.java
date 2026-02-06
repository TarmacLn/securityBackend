package com.softwareengineering.dto;

import com.softwareengineering.utils.InputFilter;
import com.softwareengineering.utils.ValidationException;

public class AvatarBody {
    public String avatar;
    
    public void validate() throws ValidationException {
        if (avatar == null || avatar.trim().isEmpty()) {
            throw new ValidationException("Avatar is required");
        }
        
        this.avatar = InputFilter.sanitizeAvatar(avatar);
    }
}
