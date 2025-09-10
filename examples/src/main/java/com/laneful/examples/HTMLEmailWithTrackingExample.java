package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;
import com.laneful.models.TrackingSettings;
import com.laneful.exceptions.*;

import java.util.Map;

public class HTMLEmailWithTrackingExample {
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
        
        try {
            // Create client
            LanefulClient client = new LanefulClient(baseUrl, authToken);
            
            // Create tracking settings
            TrackingSettings tracking = new TrackingSettings(true, true, true);
            
            // Create email
            Email email = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("HTML Email with Tracking")
                .htmlContent("<h1>Welcome!</h1><p>This is an <strong>HTML email</strong> with tracking enabled.</p><p><a href=\"https://example.com\">Click here</a> to test click tracking.</p>")
                .textContent("Welcome! This is an HTML email with tracking enabled. Visit https://example.com to test click tracking.")
                .tracking(tracking)
                .tag("welcome-email")
                .build();
            
            // Send email
            Map<String, Object> response = client.sendEmail(email);
            System.out.println("✓ HTML email with tracking sent successfully!");
            System.out.println("Response: " + response);
            
        } catch (ValidationException e) {
            System.err.println("✗ Validation error: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("✗ API error: " + e.getMessage());
            System.err.println("Status code: " + e.getStatusCode());
        } catch (HttpException e) {
            System.err.println("✗ HTTP error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
        }
    }
}