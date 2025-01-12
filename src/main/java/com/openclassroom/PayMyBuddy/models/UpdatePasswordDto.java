package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdatePasswordDto {
    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;

    @NotEmpty
    private String confirmPassword;
}
