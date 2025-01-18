package com.openclassroom.PayMyBuddy.exceptions;

/**
 * The {@code TransactionException} class is thrown when there is an error related to transactions in the system.
 */
public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
