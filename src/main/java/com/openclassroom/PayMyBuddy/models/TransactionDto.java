package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for handling transaction details.
 */
@Data
public class TransactionDto {
    @NotEmpty
    @Email
    private String receiverEmail;

    @NotEmpty
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
