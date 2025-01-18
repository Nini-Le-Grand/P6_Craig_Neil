package com.openclassroom.PayMyBuddy.exceptions;

/**
 * The {@code BalanceException} class is thrown when there is an issue related to a user's balance.
 */
public class BalanceException extends RuntimeException {
    public BalanceException(String message) {
        super(message);
    }
}
