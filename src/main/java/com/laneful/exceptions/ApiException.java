package com.laneful.exceptions;

/**
 * Exception thrown when the API returns an error response.
 */
public class ApiException extends LanefulException {
    
    private final int statusCode;
    private final String errorMessage;
    
    public ApiException(String message, int statusCode, String errorMessage) {
        super(message);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    public ApiException(String message, int statusCode, String errorMessage, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
