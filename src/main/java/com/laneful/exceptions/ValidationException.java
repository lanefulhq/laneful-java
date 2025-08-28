package com.laneful.exceptions;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends LanefulException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
