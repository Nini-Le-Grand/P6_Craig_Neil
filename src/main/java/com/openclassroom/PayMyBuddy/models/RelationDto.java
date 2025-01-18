package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for handling user relations.
 */
@Data
public class RelationDto {
    @NotEmpty
    @Email
    private String email;
}
