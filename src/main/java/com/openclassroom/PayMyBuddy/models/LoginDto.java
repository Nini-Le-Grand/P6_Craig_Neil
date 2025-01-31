package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for handling user login information.
 */
@Data
public class LoginDto {
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}
