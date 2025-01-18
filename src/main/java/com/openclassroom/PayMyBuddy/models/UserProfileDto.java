package com.openclassroom.PayMyBuddy.models;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a user's profile information.
 */
@Data
public class UserProfileDto {
    private String username;

    private String email;
}
