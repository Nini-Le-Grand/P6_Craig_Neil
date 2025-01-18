package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for handling user registration information.
 */
@Data
public class RegistrationDto {
    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String confirmPassword;

}
