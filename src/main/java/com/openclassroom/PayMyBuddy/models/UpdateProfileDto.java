package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for updating user profile information.
 */
@Data
public class UpdateProfileDto {
    private String username;

    @Email
    private String email;
}
