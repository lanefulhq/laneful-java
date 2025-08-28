package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.*;
import com.laneful.exceptions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Simple example demonstrating basic usage of the Laneful Java SDK.
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        // Replace with your actual API credentials
        String baseUrl = "https://your-endpoint.send.laneful.net";
        String authToken = "your-auth-token";
        
        // Create client
        LanefulClient client;
        try {
            client = new LanefulClient(baseUrl, authToken);
        } catch (ValidationException e) {
            System.err.println("Failed to create client: " + e.getMessage());
            return;
        }
        
        try {
            // Example 1: Simple text email
            System.out.println("Sending simple text email...");
            Email simpleEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .subject("Hello from Laneful Java SDK")
                .textContent("This is a simple test email sent using the Laneful Java SDK.")
                .build();
            
            Map<String, Object> response1 = client.sendEmail(simpleEmail);
            System.out.println("Simple email sent successfully!");
            
            // Example 2: HTML email with tracking
            System.out.println("\nSending HTML email with tracking...");
            TrackingSettings tracking = new TrackingSettings(true, true, true);
            
            Email htmlEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .subject("HTML Email with Tracking")
                .htmlContent("<h1>Welcome!</h1><p>This is an <strong>HTML email</strong> with tracking enabled.</p>")
                .textContent("Welcome! This is an HTML email with tracking enabled.")
                .tracking(tracking)
                .tag("welcome-email")
                .build();
            
            Map<String, Object> response2 = client.sendEmail(htmlEmail);
            System.out.println("HTML email sent successfully!");
            
            // Example 3: Template email
            System.out.println("\nSending template email...");
            Email templateEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .templateId("welcome-template")
                .templateData(Map.of(
                    "name", "John Doe",
                    "company", "Acme Corporation",
                    "activation_link", "https://example.com/activate"
                ))
                .build();
            
            Map<String, Object> response3 = client.sendEmail(templateEmail);
            System.out.println("Template email sent successfully!");
            
            // Example 4: Multiple recipients
            System.out.println("\nSending email to multiple recipients...");
            Email multiEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("user1@example.com", "User One"))
                .to(new Address("user2@example.com", "User Two"))
                .cc(new Address("cc@example.com", "CC Recipient"))
                .bcc(new Address("bcc@example.com", "BCC Recipient"))
                .subject("Email to Multiple Recipients")
                .textContent("This email is being sent to multiple recipients.")
                .replyTo(new Address("reply@example.com", "Reply To"))
                .build();
            
            Map<String, Object> response4 = client.sendEmail(multiEmail);
            System.out.println("Multi-recipient email sent successfully!");
            
            // Example 5: Scheduled email
            System.out.println("\nScheduling email for future delivery...");
            long sendTime = Instant.now().plusSeconds(60).getEpochSecond(); // 1 minute from now
            
            Email scheduledEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .subject("Scheduled Email")
                .textContent("This email was scheduled to be sent at a specific time.")
                .sendTime(sendTime)
                .build();
            
            Map<String, Object> response5 = client.sendEmail(scheduledEmail);
            System.out.println("Email scheduled successfully!");
            
            // Example 6: Multiple emails in batch
            System.out.println("\nSending multiple emails in batch...");
            List<Email> batchEmails = List.of(
                new Email.Builder()
                    .from(new Address("sender@example.com"))
                    .to(new Address("user1@example.com"))
                    .subject("Batch Email 1")
                    .textContent("First email in batch.")
                    .build(),
                new Email.Builder()
                    .from(new Address("sender@example.com"))
                    .to(new Address("user2@example.com"))
                    .subject("Batch Email 2")
                    .textContent("Second email in batch.")
                    .build()
            );
            
            Map<String, Object> response6 = client.sendEmails(batchEmails);
            System.out.println("Batch emails sent successfully!");
            
            System.out.println("\nAll examples completed successfully!");
            
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
        } catch (ApiException e) {
            System.err.println("API error: " + e.getMessage());
            System.err.println("Status code: " + e.getStatusCode());
            System.err.println("Error message: " + e.getErrorMessage());
        } catch (HttpException e) {
            System.err.println("HTTP error: " + e.getMessage());
            System.err.println("Status code: " + e.getStatusCode());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
