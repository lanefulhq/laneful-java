package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;
import com.laneful.exceptions.*;

import java.util.List;
import java.util.Map;

public class BatchEmailExample {
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
        if (recipients.length < 2) {
            System.err.println("❌ Need at least 2 recipient emails for batch sending");
            System.exit(1);
        }
        
        try {
            // Create client
            LanefulClient client = new LanefulClient(baseUrl, authToken);
            
            // Create multiple emails for batch sending
            List<Email> emails = List.of(
                new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    .to(new Address(recipients[0].trim(), "User One"))
                    .subject("Batch Email 1")
                    .textContent("This is the first email in the batch.")
                    .build(),
                new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    .to(new Address(recipients[1].trim(), "User Two"))
                    .subject("Batch Email 2")
                    .textContent("This is the second email in the batch.")
                    .build()
            );
            
            // Send batch emails
            Map<String, Object> response = client.sendEmails(emails);
            System.out.println("✓ Batch emails sent successfully!");
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