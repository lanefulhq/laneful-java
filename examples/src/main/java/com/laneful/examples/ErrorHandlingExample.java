package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;
import com.laneful.exceptions.*;

import java.util.Map;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        // Get configuration from environment variables
        String baseUrl = System.getenv("LANEFUL_BASE_URL");
        String authToken = System.getenv("LANEFUL_AUTH_TOKEN");
        String fromEmail = System.getenv("LANEFUL_FROM_EMAIL");
        String toEmails = System.getenv("LANEFUL_TO_EMAILS");
        
        if (baseUrl == null || authToken == null || fromEmail == null || toEmails == null) {
            System.err.println("❌ Missing required environment variables:");
            System.err.println("   LANEFUL_BASE_URL, LANEFUL_AUTH_TOKEN, LANEFUL_FROM_EMAIL, LANEFUL_TO_EMAILS");
            System.exit(1);
        }
        
        // Parse recipient emails (comma-separated)
        String[] recipients = toEmails.split(",");
        if (recipients.length == 0) {
            System.err.println("❌ No recipient emails provided");
            System.exit(1);
        }
        
        System.out.println("🔍 Error Handling Example");
        System.out.println("=========================");
        
        try {
            // Create client
            LanefulClient client = new LanefulClient(baseUrl, authToken);
            
            // Test 1: Valid email
            System.out.println("Test 1: Sending valid email...");
            Email validEmail = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("Valid Email Test")
                .textContent("This is a valid email for testing error handling.")
                .build();
            
            Map<String, Object> response = client.sendEmail(validEmail);
            System.out.println("✓ Valid email sent successfully!");
            System.out.println("Response: " + response);
            System.out.println();
            
            // Test 2: Invalid email address (should trigger ValidationException)
            System.out.println("Test 2: Testing invalid email address...");
            try {
                Email invalidEmail = new Email.Builder()
                    .from(new Address("invalid-email", "Invalid Sender"))
                    .to(new Address(recipients[0].trim(), "Test Recipient"))
                    .subject("Invalid Email Test")
                    .textContent("This should fail due to invalid sender email.")
                    .build();
                
                client.sendEmail(invalidEmail);
                System.out.println("⚠️  Unexpected: Invalid email was accepted");
            } catch (ValidationException e) {
                System.out.println("✓ Caught expected ValidationException: " + e.getMessage());
            }
            System.out.println();
            
            // Test 3: Missing required fields (should trigger ValidationException)
            System.out.println("Test 3: Testing missing required fields...");
            try {
                Email incompleteEmail = new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    // Missing .to() field
                    .subject("Incomplete Email Test")
                    .textContent("This should fail due to missing recipient.")
                    .build();
                
                client.sendEmail(incompleteEmail);
                System.out.println("⚠️  Unexpected: Incomplete email was accepted");
            } catch (ValidationException e) {
                System.out.println("✓ Caught expected ValidationException: " + e.getMessage());
            }
            System.out.println();
            
            // Test 4: Test with invalid auth token (should trigger ApiException)
            System.out.println("Test 4: Testing with invalid auth token...");
            try {
                LanefulClient invalidClient = new LanefulClient(baseUrl, "invalid-token");
                Email testEmail = new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    .to(new Address(recipients[0].trim(), "Test Recipient"))
                    .subject("Invalid Token Test")
                    .textContent("This should fail due to invalid auth token.")
                    .build();
                
                invalidClient.sendEmail(testEmail);
                System.out.println("⚠️  Unexpected: Invalid token was accepted");
            } catch (ApiException e) {
                System.out.println("✓ Caught expected ApiException: " + e.getMessage());
                System.out.println("  Status code: " + e.getStatusCode());
            }
            System.out.println();
            
            System.out.println("✓ Error handling example completed successfully!");
            
        } catch (ValidationException e) {
            System.err.println("✗ Validation error: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("✗ API error: " + e.getMessage());
            System.err.println("Status code: " + e.getStatusCode());
        } catch (HttpException e) {
            System.err.println("✗ HTTP error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}