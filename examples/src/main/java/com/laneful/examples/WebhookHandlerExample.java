package com.laneful.examples;

import com.laneful.webhooks.WebhookVerifier;
import com.laneful.exceptions.*;

import java.util.Map;

public class WebhookHandlerExample {
    public static void main(String[] args) {
        // Get configuration from environment variables
        String webhookSecret = System.getenv("LANEFUL_WEBHOOK_SECRET");
        String webhookUrl = System.getenv("LANEFUL_WEBHOOK_URL");
        
        if (webhookSecret == null || webhookUrl == null) {
            System.err.println("❌ Missing required environment variables:");
            System.err.println("   LANEFUL_WEBHOOK_SECRET, LANEFUL_WEBHOOK_URL");
            System.exit(1);
        }
        
        // Sample webhook payload (in real usage, this would come from HTTP request)
        // Using array format as expected by WebhookVerifier
        String samplePayload = """
            [
                {
                    "event": "delivery",
                    "email": "user@example.com",
                    "lane_id": "5805dd85-ed8c-44db-91a7-1d53a41c86a5",
                    "message_id": "H-1-019844e340027d728a7cfda632e14d0a",
                    "timestamp": 1640995200
                },
                {
                    "event": "open",
                    "email": "user@example.com",
                    "lane_id": "5805dd85-ed8c-44db-91a7-1d53a41c86a5",
                    "message_id": "H-1-019844e340027d728a7cfda632e14d0b",
                    "timestamp": 1640995260
                }
            ]
            """;
        
        // Generate a valid signature using the webhook secret (like in Python tests)
        String validSignature;
        try {
            validSignature = WebhookVerifier.generateSignature(webhookSecret, samplePayload, true);
        } catch (Exception e) {
            System.err.println("❌ Failed to generate signature: " + e.getMessage());
            return;
        }
        
        // Sample headers with valid signature (in real usage, these would come from HTTP request)
        Map<String, String> sampleHeaders = Map.of(
            "x-webhook-signature", validSignature,
            "Content-Type", "application/json"
        );
        
        try {
            System.out.println("🔍 Webhook Handler Example");
            System.out.println("==========================");
            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Webhook Secret: " + webhookSecret.substring(0, 8) + "...");
            System.out.println();
            
            // Extract signature from headers
            String signature = WebhookVerifier.extractSignatureFromHeaders(sampleHeaders);
            if (signature == null) {
                System.err.println("❌ Missing signature in headers");
                return;
            }
            System.out.println("✓ Signature extracted: " + signature);
            
            // Verify the signature using the webhook secret
            boolean isValid = WebhookVerifier.verifySignature(webhookSecret, samplePayload, signature);
            if (!isValid) {
                System.err.println("❌ Invalid signature - webhook may be compromised");
                return;
            }
            System.out.println("✓ Signature verified successfully");
            
            // Parse and validate payload
            WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(samplePayload);
            System.out.println("✓ Payload parsed successfully");
            
            // Process events
            System.out.println("📧 Processing " + webhookData.getEvents().size() + " events:");
            for (Map<String, Object> event : webhookData.getEvents()) {
                String eventType = (String) event.get("event");
                String email = (String) event.get("email");
                
                switch (eventType) {
                    case "delivery":
                        System.out.println("  ✓ Email delivered to: " + email);
                        break;
                    case "open":
                        System.out.println("  👁️  Email opened by: " + email);
                        break;
                    case "click":
                        String url = (String) event.get("url");
                        System.out.println("  🔗 Link clicked by " + email + ": " + url);
                        break;
                    case "bounce":
                        Boolean isHard = (Boolean) event.get("is_hard");
                        System.out.println("  📧 Email bounced (" + (isHard ? "hard" : "soft") + ") for: " + email);
                        break;
                    case "unsubscribe":
                        System.out.println("  🚫 User unsubscribed: " + email);
                        break;
                    default:
                        System.out.println("  ❓ Unknown event type: " + eventType + " for: " + email);
                }
            }
            
            System.out.println();
            System.out.println("✓ Webhook processed successfully!");
            
        } catch (IllegalArgumentException e) {
            System.err.println("✗ Invalid payload: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Processing error: " + e.getMessage());
        }
    }
}