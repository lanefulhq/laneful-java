package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;
import com.laneful.models.Attachment;
import com.laneful.exceptions.*;

import java.nio.file.Paths;
import java.util.Map;

public class AttachmentEmailExample {
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
            
            // Create a simple text attachment (since we don't have a real file)
            Attachment attachment = new Attachment(
                "test-document.txt",
                "text/plain",
                "VGhpcyBpcyBhIHRlc3QgZG9jdW1lbnQgYXR0YWNobWVudC4=" // Base64 encoded "This is a test document attachment."
            );
            
            // Create email with attachment
            Email email = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("Document Attached")
                .textContent("Please find the document attached.")
                .attachment(attachment)
                .build();
            
            // Send email
            Map<String, Object> response = client.sendEmail(email);
            System.out.println("✓ Email with attachment sent successfully!");
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