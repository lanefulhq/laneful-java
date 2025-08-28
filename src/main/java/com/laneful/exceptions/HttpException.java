package com.laneful.exceptions;

/**
 * Exception thrown when HTTP communication fails.
 */
public class HttpException extends LanefulException {
    
    private final int statusCode;
    
    public HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public HttpException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
