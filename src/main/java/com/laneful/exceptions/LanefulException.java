package com.laneful.exceptions;

/**
 * Base exception class for all Laneful SDK exceptions.
 */
public abstract class LanefulException extends Exception {
    
    public LanefulException(String message) {
        super(message);
    }
    
    public LanefulException(String message, Throwable cause) {
        super(message, cause);
    }
}
