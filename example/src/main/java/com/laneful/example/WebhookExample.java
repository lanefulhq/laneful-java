package com.laneful.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laneful.webhooks.WebhookVerifier;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Laneful Webhook Handler Example
 * 
 * This example demonstrates how to properly handle Laneful webhooks
 * according to the documentation, similar to the PHP implementation.
 */
@WebServlet("/webhook")
public class WebhookExample extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(WebhookExample.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Configuration - in production, load from environment variables or config
    private static final String WEBHOOK_SECRET = System.getenv("LANEFUL_WEBHOOK_SECRET") != null 
        ? System.getenv("LANEFUL_WEBHOOK_SECRET") 
        : "your-webhook-secret-here";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Step 1: Get raw payload
            String payload = getRequestBody(request);
            if (payload == null || payload.trim().isEmpty()) {
                throw new Exception("Empty payload received");
            }
            
            // Step 2: Extract signature from headers (as documented)
            String signature = extractSignatureFromRequest(request);
            if (signature == null) {
                throw new Exception("Missing webhook signature header");
            }
            
            // Step 3: Verify signature (supports sha256= prefix as documented)
            if (!WebhookVerifier.verifySignature(WEBHOOK_SECRET, payload, signature)) {
                throw new Exception("Invalid webhook signature");
            }
            
            // Step 4: Parse and validate payload structure
            WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(payload);
            
            // Step 5: Process events (handles both batch and single event formats)
            int processedCount = 0;
            for (Map<String, Object> event : webhookData.getEvents()) {
                processWebhookEvent(event);
                processedCount++;
            }
            
            // Log successful processing
            logger.info(String.format(
                "Successfully processed %d webhook event(s) in %s mode",
                processedCount,
                webhookData.isBatch() ? "batch" : "single"
            ));
            
            // Return success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("processed", processedCount);
            responseData.put("mode", webhookData.isBatch() ? "batch" : "single");
            
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(responseData));
            out.flush();
            
        } catch (IllegalArgumentException e) {
            // Payload validation error
            logger.severe("Webhook payload validation error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid payload: " + e.getMessage());
            
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(errorResponse));
            out.flush();
            
        } catch (Exception e) {
            // Other errors (signature, missing data, etc.)
            logger.severe("Webhook processing error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            PrintWriter out = response.getWriter();
            out.print(objectMapper.writeValueAsString(errorResponse));
            out.flush();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Show usage information for GET requests
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("    <title>Laneful Webhook Endpoint</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; }");
        out.println("        .code { background: #f5f5f5; padding: 15px; border-radius: 5px; font-family: monospace; }");
        out.println("        .success { color: #28a745; }");
        out.println("        .info { background: #e7f3ff; padding: 15px; border-radius: 5px; border-left: 4px solid #0066cc; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>🚀 Laneful Webhook Endpoint</h1>");
        out.println("    <p>This endpoint is ready to receive Laneful webhook events.</p>");
        out.println("    ");
        out.println("    <div class=\"info\">");
        out.println("        <h3>Webhook Configuration</h3>");
        out.println("        <p><strong>Header:</strong> " + WebhookVerifier.getSignatureHeaderName() + "</p>");
        out.println("        <p><strong>Supported Events:</strong> delivery, open, click, bounce, drop, spam_complaint, unsubscribe</p>");
        out.println("        <p><strong>Payload Formats:</strong> Single event (object) or Batch mode (array)</p>");
        out.println("    </div>");
        out.println("    ");
        out.println("    <h3>Test Webhook Verification</h3>");
        out.println("    <div class=\"code\">");
        out.println("curl -X POST " + request.getRequestURL() + " \\");
        out.println("  -H \"Content-Type: application/json\" \\");
        out.println("  -H \"" + WebhookVerifier.getSignatureHeaderName() + ": sha256=...\" \\");
        out.println("  -d '{\"event\":\"delivery\",\"email\":\"test@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"test\",\"timestamp\":" + System.currentTimeMillis() / 1000 + "}'");
        out.println("    </div>");
        out.println("    ");
        out.println("    <h3>Implementation Features</h3>");
        out.println("    <ul>");
        out.println("        <li class=\"success\">✅ Signature verification with sha256= prefix support</li>");
        out.println("        <li class=\"success\">✅ Batch and single event mode detection</li>");
        out.println("        <li class=\"success\">✅ Payload structure validation</li>");
        out.println("        <li class=\"success\">✅ All documented event types supported</li>");
        out.println("        <li class=\"success\">✅ Header extraction with fallback formats</li>");
        out.println("        <li class=\"success\">✅ Comprehensive error handling</li>");
        out.println("    </ul>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Process individual webhook events according to documentation
     */
    private void processWebhookEvent(Map<String, Object> event) {
        String eventType = (String) event.get("event");
        String email = (String) event.get("email");
        String messageId = (String) event.get("message_id");
        String timestamp = (String) event.get("timestamp");
        
        // Log basic event info
        logger.info(String.format("Processing %s event for %s (Message ID: %s)", eventType, email, messageId));
        
        // Process based on event type (all types from documentation)
        switch (eventType) {
            case "delivery":
                handleDeliveryEvent(event);
                break;
                
            case "open":
                handleOpenEvent(event);
                break;
                
            case "click":
                handleClickEvent(event);
                break;
                
            case "bounce":
                handleBounceEvent(event);
                break;
                
            case "drop":
                handleDropEvent(event);
                break;
                
            case "spam_complaint":
                handleSpamComplaintEvent(event);
                break;
                
            case "unsubscribe":
                handleUnsubscribeEvent(event);
                break;
                
            default:
                logger.warning("Unknown event type: " + eventType);
        }
    }
    
    /**
     * Handle delivery events
     */
    private void handleDeliveryEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        String messageId = (String) event.get("message_id");
        
        // Update delivery status in your database
        // Example: markEmailAsDelivered(messageId, Long.parseLong((String) event.get("timestamp")));
        
        logger.info("Email delivered successfully to " + email);
    }
    
    /**
     * Handle open events
     */
    private void handleOpenEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        String device = (String) event.getOrDefault("client_device", "Unknown");
        String os = (String) event.getOrDefault("client_os", "Unknown");
        String ip = (String) event.getOrDefault("client_ip", "Unknown");
        
        logger.info(String.format("Email opened by %s on %s (%s)", email, device, os));
        
        // Example: trackEmailOpen((String) event.get("message_id"), device, os, ip, Long.parseLong((String) event.get("timestamp")));
    }
    
    /**
     * Handle click events
     */
    private void handleClickEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        String url = (String) event.getOrDefault("url", "Unknown URL");
        
        logger.info("Link clicked in email to " + email + ": " + url);
        
        // Example: trackLinkClick((String) event.get("message_id"), url, Long.parseLong((String) event.get("timestamp")));
    }
    
    /**
     * Handle bounce events
     */
    private void handleBounceEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        Boolean isHard = (Boolean) event.getOrDefault("is_hard", false);
        String reason = (String) event.getOrDefault("text", "Unknown reason");
        
        String bounceType = isHard ? "hard" : "soft";
        logger.info(String.format("Email bounced (%s) for %s: %s", bounceType, email, reason));
        
        // Handle hard bounces by suppressing the email
        if (isHard) {
            // Example: suppressEmail(email, "hard_bounce");
        }
    }
    
    /**
     * Handle drop events
     */
    private void handleDropEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        String reason = (String) event.getOrDefault("reason", "Unknown reason");
        
        logger.info("Email dropped for " + email + ": " + reason);
        
        // Example: handleEmailDrop((String) event.get("message_id"), reason);
    }
    
    /**
     * Handle spam complaint events
     */
    private void handleSpamComplaintEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        
        logger.info("Spam complaint received for " + email);
        
        // Automatically unsubscribe users who mark emails as spam
        // Example: unsubscribeEmail(email, "spam_complaint");
    }
    
    /**
     * Handle unsubscribe events
     */
    private void handleUnsubscribeEvent(Map<String, Object> event) {
        String email = (String) event.get("email");
        String groupId = (String) event.get("unsubscribe_group_id");
        
        logger.info("Unsubscribe event for " + email + (groupId != null ? " (Group: " + groupId + ")" : ""));
        
        // Example: processUnsubscribe(email, groupId);
    }
    
    /**
     * Extract webhook signature from HTTP request headers
     */
    private String extractSignatureFromRequest(HttpServletRequest request) {
        // Try documented header name first
        String signature = request.getHeader(WebhookVerifier.getSignatureHeaderName());
        if (signature != null) {
            return signature;
        }
        
        // Try uppercase version
        String upperHeader = WebhookVerifier.getSignatureHeaderName().toUpperCase().replace("-", "_");
        signature = request.getHeader(upperHeader);
        if (signature != null) {
            return signature;
        }
        
        return null;
    }
    
    /**
     * Get request body as string
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}
