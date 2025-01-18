package com.openclassroom.PayMyBuddy.exceptions;

import com.openclassroom.PayMyBuddy.models.RegistrationDto;
import lombok.Getter;

/**
 * The {@code RegistrationException} class is thrown when there is an error during the user registration process.
 */
@Getter
public class RegistrationException extends RuntimeException {
    private final RegistrationDto registrationDto;

    public RegistrationException(String message, RegistrationDto registrationDto) {
        super(message);
        this.registrationDto = registrationDto;
    }
}
