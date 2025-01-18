package com.openclassroom.PayMyBuddy.exceptions;

import lombok.Getter;

/**
 * The {@code ProfileUpdateException} class is thrown when there is an error updating a user's profile.
 */
@Getter
public class ProfileUpdateException extends RuntimeException {
    public ProfileUpdateException(String message) {
        super(message);
    }
}
