package com.openclassroom.PayMyBuddy.exceptions;

import lombok.Getter;

@Getter
public class ProfileUpdateException extends RuntimeException {
    public ProfileUpdateException(String message) {
        super(message);
    }
}
