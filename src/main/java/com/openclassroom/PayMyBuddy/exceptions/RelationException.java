package com.openclassroom.PayMyBuddy.exceptions;

/**
 * The {@code RelationException} class is thrown when there is an error related to user relations in the system.
 */
public class RelationException extends RuntimeException {
    public RelationException(String message) {
        super(message);
    }
}
