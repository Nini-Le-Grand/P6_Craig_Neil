package com.openclassroom.PayMyBuddy.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for handling user balance information.
 */
@Data
public class BalanceDto {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
