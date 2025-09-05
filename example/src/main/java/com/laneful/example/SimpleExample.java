package com.laneful.example;

import com.laneful.client.LanefulClient;
import com.laneful.models.*;
import com.laneful.exceptions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Simple example demonstrating basic usage of the Laneful Java SDK.
 * 
 * This example shows how to use the published Laneful Java SDK from Maven Central.
 * 
 * Prerequisites:
 * 1. Replace the baseUrl and authToken with your actual credentials
 * 2. Ensure you have a valid Laneful account and API access
 * 
 * To run this example:
 * mvn compile exec:java
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
            System.out.println("✓ Laneful client created successfully");
        } catch (ValidationException e) {
            System.err.println("✗ Failed to create client: " + e.getMessage());
            System.err.println("Please check your baseUrl and authToken");
            return;
        }
        
        try {
            // Example 1: Simple text email
            System.out.println("\n📧 Example 1: Sending simple text email...");
            Email simpleEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .subject("Hello from Laneful Java SDK")
                .textContent("This is a simple test email sent using the Laneful Java SDK.")
                .build();
            
            Map<String, Object> response1 = client.sendEmail(simpleEmail);
            System.out.println("✓ Simple email sent successfully!");
            System.out.println("  Response: " + response1);
            
            // Example 2: HTML email with tracking
            System.out.println("\n📧 Example 2: Sending HTML email with tracking...");
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
            System.out.println("✓ HTML email sent successfully!");
            System.out.println("  Response: " + response2);
            
            // Example 3: Template email
            System.out.println("\n📧 Example 3: Sending template email...");
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
            System.out.println("✓ Template email sent successfully!");
            System.out.println("  Response: " + response3);
            
            // Example 4: Multiple recipients
            System.out.println("\n📧 Example 4: Sending email to multiple recipients...");
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
            System.out.println("✓ Multi-recipient email sent successfully!");
            System.out.println("  Response: " + response4);
            
            // Example 5: Scheduled email
            System.out.println("\n📧 Example 5: Scheduling email for future delivery...");
            long sendTime = Instant.now().plusSeconds(60).getEpochSecond(); // 1 minute from now
            
            Email scheduledEmail = new Email.Builder()
                .from(new Address("sender@example.com", "Your Name"))
                .to(new Address("recipient@example.com", "Recipient Name"))
                .subject("Scheduled Email")
                .textContent("This email was scheduled to be sent at a specific time.")
                .sendTime(sendTime)
                .build();
            
            Map<String, Object> response5 = client.sendEmail(scheduledEmail);
            System.out.println("✓ Email scheduled successfully!");
            System.out.println("  Response: " + response5);
            
            // Example 6: Multiple emails in batch
            System.out.println("\n📧 Example 6: Sending multiple emails in batch...");
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
            System.out.println("✓ Batch emails sent successfully!");
            System.out.println("  Response: " + response6);
            
            System.out.println("\n🎉 All examples completed successfully!");
            System.out.println("The Laneful Java SDK is working correctly with the published library from Maven Central!");
            
        } catch (ValidationException e) {
            System.err.println("✗ Validation error: " + e.getMessage());
            System.err.println("Please check your email configuration");
        } catch (ApiException e) {
            System.err.println("✗ API error: " + e.getMessage());
            System.err.println("  Status code: " + e.getStatusCode());
            System.err.println("  Error message: " + e.getErrorMessage());
            System.err.println("Please check your API credentials and endpoint");
        } catch (HttpException e) {
            System.err.println("✗ HTTP error: " + e.getMessage());
            System.err.println("  Status code: " + e.getStatusCode());
            System.err.println("Please check your network connection and endpoint URL");
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
