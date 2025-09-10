package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;
import com.laneful.models.TrackingSettings;
import com.laneful.models.Attachment;
import com.laneful.exceptions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ComprehensiveExample {
    public static void main(String[] args) {
        // Get configuration from environment variables
        String baseUrl = System.getenv("LANEFUL_BASE_URL");
        String authToken = System.getenv("LANEFUL_AUTH_TOKEN");
        String fromEmail = System.getenv("LANEFUL_FROM_EMAIL");
        String toEmails = System.getenv("LANEFUL_TO_EMAILS");
        String templateId = System.getenv("LANEFUL_TEMPLATE_ID");
        
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
        
        System.out.println("🚀 Comprehensive Laneful Java SDK Example");
        System.out.println("==========================================");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("From Email: " + fromEmail);
        System.out.println("Recipients: " + toEmails);
        System.out.println();
        
        try {
            // Create client
            LanefulClient client = new LanefulClient(baseUrl, authToken);
            
            // Example 1: Basic Text Email
            System.out.println("📧 Example 1: Basic Text Email");
            System.out.println("-------------------------------");
            Email basicEmail = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("Basic Text Email")
                .textContent("This is a basic text email sent using the Laneful Java SDK.")
                .build();
            
            Map<String, Object> response1 = client.sendEmail(basicEmail);
            System.out.println("✓ Basic email sent successfully!");
            System.out.println("Response: " + response1);
            System.out.println();
            
            // Example 2: HTML Email with Tracking
            System.out.println("📧 Example 2: HTML Email with Tracking");
            System.out.println("--------------------------------------");
            TrackingSettings tracking = new TrackingSettings(true, true, true);
            Email htmlEmail = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("HTML Email with Tracking")
                .htmlContent("<h1>Welcome!</h1><p>This is an <strong>HTML email</strong> with tracking enabled.</p><p><a href=\"https://example.com\">Click here</a> to test click tracking.</p>")
                .textContent("Welcome! This is an HTML email with tracking enabled. Visit https://example.com to test click tracking.")
                .tracking(tracking)
                .tag("comprehensive-example")
                .build();
            
            Map<String, Object> response2 = client.sendEmail(htmlEmail);
            System.out.println("✓ HTML email with tracking sent successfully!");
            System.out.println("Response: " + response2);
            System.out.println();
            
            // Example 3: Email with Attachment
            System.out.println("📧 Example 3: Email with Attachment");
            System.out.println("-----------------------------------");
            Attachment attachment = new Attachment(
                "test-document.txt",
                "text/plain",
                "VGhpcyBpcyBhIHRlc3QgZG9jdW1lbnQgYXR0YWNobWVudC4=" // Base64 encoded "This is a test document attachment."
            );
            
            Email attachmentEmail = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("Email with Attachment")
                .textContent("Please find the document attached.")
                .attachment(attachment)
                .build();
            
            Map<String, Object> response3 = client.sendEmail(attachmentEmail);
            System.out.println("✓ Email with attachment sent successfully!");
            System.out.println("Response: " + response3);
            System.out.println();
            
            // Example 4: Multiple Recipients
            if (recipients.length > 1) {
                System.out.println("📧 Example 4: Multiple Recipients");
                System.out.println("---------------------------------");
                Email.Builder multiEmailBuilder = new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    .subject("Email to Multiple Recipients")
                    .textContent("This email is being sent to multiple recipients.");
                
                // Add TO recipients
                for (int i = 0; i < Math.min(recipients.length, 2); i++) {
                    multiEmailBuilder.to(new Address(recipients[i].trim(), "User " + (i + 1)));
                }
                
                // Add CC if we have more recipients
                if (recipients.length > 2) {
                    multiEmailBuilder.cc(new Address(recipients[2].trim(), "CC Recipient"));
                }
                
                Email multiEmail = multiEmailBuilder.build();
                
                Map<String, Object> response4 = client.sendEmail(multiEmail);
                System.out.println("✓ Email to multiple recipients sent successfully!");
                System.out.println("Response: " + response4);
                System.out.println();
            }
            
            // Example 5: Template Email (if template ID is provided)
            if (templateId != null) {
                System.out.println("📧 Example 5: Template Email");
                System.out.println("----------------------------");
                Email templateEmail = new Email.Builder()
                    .from(new Address(fromEmail, "Laneful SDK"))
                    .to(new Address(recipients[0].trim(), "Test Recipient"))
                    .subject("Template Email")
                    .templateId(templateId)
                    .templateData(Map.of(
                        "name", "John Doe",
                        "company", "Acme Corporation",
                        "activation_link", "https://example.com/activate"
                    ))
                    .build();
                
                Map<String, Object> response5 = client.sendEmail(templateEmail);
                System.out.println("✓ Template email sent successfully!");
                System.out.println("Response: " + response5);
                System.out.println();
            }
            
            // Example 6: Scheduled Email
            System.out.println("📧 Example 6: Scheduled Email");
            System.out.println("-----------------------------");
            long sendTime = Instant.now().plusSeconds(120).getEpochSecond(); // 2 minutes from now
            
            Email scheduledEmail = new Email.Builder()
                .from(new Address(fromEmail, "Laneful SDK"))
                .to(new Address(recipients[0].trim(), "Test Recipient"))
                .subject("Scheduled Email")
                .textContent("This email was scheduled to be sent 2 minutes after the request was made.")
                .sendTime(sendTime)
                .build();
            
            Map<String, Object> response6 = client.sendEmail(scheduledEmail);
            System.out.println("✓ Scheduled email sent successfully!");
            System.out.println("Scheduled for: " + Instant.ofEpochSecond(sendTime));
            System.out.println("Response: " + response6);
            System.out.println();
            
            // Example 7: Batch Email Sending
            if (recipients.length > 1) {
                System.out.println("📧 Example 7: Batch Email Sending");
                System.out.println("---------------------------------");
                List<Email> batchEmails = List.of(
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
                
                Map<String, Object> response7 = client.sendEmails(batchEmails);
                System.out.println("✓ Batch emails sent successfully!");
                System.out.println("Response: " + response7);
                System.out.println();
            }
            
            System.out.println("🎉 Comprehensive example completed successfully!");
            System.out.println("All email types have been demonstrated.");
            
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