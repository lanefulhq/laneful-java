package com.laneful.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class for verifying webhook signatures and processing webhook payloads.
 */
public class WebhookVerifier {
    
    private static final String ALGORITHM = "HmacSHA256";
    private static final String SIGNATURE_PREFIX = "sha256=";
    private static final String SIGNATURE_HEADER_NAME = "x-webhook-signature";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    // Valid event types as documented
    private static final Set<String> VALID_EVENT_TYPES = Set.of(
        "delivery", "open", "click", "drop", "spam_complaint", "unsubscribe", "bounce"
    );
    
    // UUID pattern for lane_id validation
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Verifies a webhook signature with support for sha256= prefix.
     * 
     * @param secret The webhook secret
     * @param payload The webhook payload
     * @param signature The signature to verify (may include 'sha256=' prefix)
     * @return true if the signature is valid, false otherwise
     */
    public static boolean verifySignature(String secret, String payload, String signature) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret cannot be empty");
        }
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        if (signature == null || signature.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Handle sha256= prefix as documented
            String cleanSignature = signature.startsWith(SIGNATURE_PREFIX) 
                ? signature.substring(SIGNATURE_PREFIX.length())
                : signature;
                
            String expectedSignature = generateSignature(secret, payload);
            return constantTimeEquals(expectedSignature, cleanSignature);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generates a signature for the given payload.
     * 
     * @param secret The webhook secret
     * @param payload The payload to sign
     * @return The generated signature
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeyException If the key is invalid
     */
    public static String generateSignature(String secret, String payload) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), ALGORITHM
        );
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    /**
     * Generates a signature for the given payload with optional prefix.
     * 
     * @param secret The webhook secret
     * @param payload The payload to sign
     * @param includePrefix Whether to include the 'sha256=' prefix
     * @return The generated signature
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeyException If the key is invalid
     */
    public static String generateSignature(String secret, String payload, boolean includePrefix) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = generateSignature(secret, payload);
        return includePrefix ? SIGNATURE_PREFIX + signature : signature;
    }
    
    /**
     * Parse and validate webhook payload structure.
     * 
     * @param payload The raw webhook payload JSON
     * @return WebhookData containing parsed events and batch mode flag
     * @throws IllegalArgumentException If payload is invalid JSON or structure
     */
    public static WebhookData parseWebhookPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
        
        try {
            JsonNode data = OBJECT_MAPPER.readTree(payload);
            
            if (data.isArray()) {
                // Batch mode: array of events
                List<Map<String, Object>> events = new ArrayList<>();
                for (JsonNode event : data) {
                    Map<String, Object> eventMap = validateAndParseEvent(event);
                    events.add(eventMap);
                }
                return new WebhookData(true, events);
            } else if (data.isObject()) {
                // Single event mode
                Map<String, Object> eventMap = validateAndParseEvent(data);
                return new WebhookData(false, List.of(eventMap));
            } else {
                throw new IllegalArgumentException("Invalid webhook payload structure");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON payload: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Invalid JSON payload: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate individual event structure according to documentation.
     * 
     * @param event The event JSON node
     * @return Parsed event as Map
     * @throws IllegalArgumentException If event structure is invalid
     */
    private static Map<String, Object> validateAndParseEvent(JsonNode event) {
        if (!event.isObject()) {
            throw new IllegalArgumentException("Event must be an object");
        }
        
        ObjectNode eventObj = (ObjectNode) event;
        Map<String, Object> eventMap = new HashMap<>();
        
        // Required fields
        String[] requiredFields = {"event", "email", "lane_id", "message_id", "timestamp"};
        for (String field : requiredFields) {
            if (!eventObj.has(field)) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
            eventMap.put(field, eventObj.get(field).asText());
        }
        
        // Validate event type
        String eventType = eventMap.get("event").toString();
        if (!VALID_EVENT_TYPES.contains(eventType)) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
        
        // Validate email format
        String email = eventMap.get("email").toString();
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        
        // Validate timestamp is numeric
        String timestamp = eventMap.get("timestamp").toString();
        try {
            Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp format");
        }
        
        // Validate lane_id is a valid UUID format
        String laneId = eventMap.get("lane_id").toString();
        if (!UUID_PATTERN.matcher(laneId).matches()) {
            throw new IllegalArgumentException("Invalid lane_id format: " + laneId);
        }
        
        // Add optional fields
        if (eventObj.has("metadata")) {
            eventMap.put("metadata", OBJECT_MAPPER.convertValue(eventObj.get("metadata"), Map.class));
        }
        if (eventObj.has("tag")) {
            eventMap.put("tag", eventObj.get("tag").asText());
        }
        if (eventObj.has("url")) {
            eventMap.put("url", eventObj.get("url").asText());
        }
        if (eventObj.has("is_hard")) {
            eventMap.put("is_hard", eventObj.get("is_hard").asBoolean());
        }
        if (eventObj.has("text")) {
            eventMap.put("text", eventObj.get("text").asText());
        }
        if (eventObj.has("reason")) {
            eventMap.put("reason", eventObj.get("reason").asText());
        }
        if (eventObj.has("unsubscribe_group_id")) {
            eventMap.put("unsubscribe_group_id", eventObj.get("unsubscribe_group_id").asText());
        }
        if (eventObj.has("client_device")) {
            eventMap.put("client_device", eventObj.get("client_device").asText());
        }
        if (eventObj.has("client_os")) {
            eventMap.put("client_os", eventObj.get("client_os").asText());
        }
        if (eventObj.has("client_ip")) {
            eventMap.put("client_ip", eventObj.get("client_ip").asText());
        }
        
        return eventMap;
    }
    
    /**
     * Simple email validation.
     * 
     * @param email The email to validate
     * @return true if email format is valid
     */
    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    /**
     * Get the webhook header name as documented.
     * 
     * @return The correct header name for webhook signatures
     */
    public static String getSignatureHeaderName() {
        return SIGNATURE_HEADER_NAME;
    }
    
    /**
     * Extract webhook signature from HTTP headers (supports multiple formats).
     * 
     * @param headers HTTP headers map
     * @return The webhook signature or null if not found
     */
    public static String extractSignatureFromHeaders(Map<String, String> headers) {
        if (headers == null) {
            return null;
        }
        
        // Try documented header name first
        String signature = headers.get(SIGNATURE_HEADER_NAME);
        if (signature != null) {
            return signature;
        }
        
        // Try uppercase version
        String upperHeader = SIGNATURE_HEADER_NAME.toUpperCase().replace("-", "_");
        signature = headers.get(upperHeader);
        if (signature != null) {
            return signature;
        }
        
        // Try with HTTP_ prefix (common in servlet environments)
        String serverHeader = "HTTP_" + upperHeader;
        signature = headers.get(serverHeader);
        if (signature != null) {
            return signature;
        }
        
        return null;
    }
    
    /**
     * Compares two strings in constant time to prevent timing attacks.
     * 
     * @param a First string
     * @param b Second string
     * @return true if strings are equal, false otherwise
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
    
    /**
     * Converts a byte array to a hexadecimal string.
     * 
     * @param bytes The byte array
     * @return Hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Data class representing parsed webhook payload.
     */
    public static class WebhookData {
        private final boolean isBatch;
        private final List<Map<String, Object>> events;
        
        public WebhookData(boolean isBatch, List<Map<String, Object>> events) {
            this.isBatch = isBatch;
            this.events = events;
        }
        
        public boolean isBatch() {
            return isBatch;
        }
        
        public List<Map<String, Object>> getEvents() {
            return events;
        }
        
        @Override
        public String toString() {
            return "WebhookData{" +
                    "isBatch=" + isBatch +
                    ", events=" + events +
                    '}';
        }
    }
}
