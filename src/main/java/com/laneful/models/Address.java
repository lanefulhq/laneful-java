package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents an email address with an optional name.
 */
public record Address(
    @JsonProperty("email") String email,
    @JsonProperty("name") String name
) {
    
    /**
     * Creates a new Address with email and optional name.
     * 
     * @param email The email address (required)
     * @param name The display name (optional)
     * @throws IllegalArgumentException if email is null or invalid
     */
    public Address {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty");
        }
        
        // Basic email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address format: " + email);
        }
    }
    
    /**
     * Creates a new Address with just an email address.
     * 
     * @param email The email address (required)
     * @throws IllegalArgumentException if email is null or invalid
     */
    public Address(String email) {
        this(email, null);
    }
    
    /**
     * Creates an Address from a map representation.
     * 
     * @param data Map containing email and optional name
     * @return New Address instance
     * @throws IllegalArgumentException if email is null or invalid
     */
    public static Address fromMap(java.util.Map<String, Object> data) throws IllegalArgumentException {
        String email = (String) data.get("email");
        String name = (String) data.get("name");
        return new Address(email, name);
    }
    
    @Override
    public String toString() {
        if (name != null && !name.trim().isEmpty()) {
            return name + " <" + email + ">";
        }
        return email;
    }
}
