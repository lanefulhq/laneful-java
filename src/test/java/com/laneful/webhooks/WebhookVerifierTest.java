package com.laneful.webhooks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for WebhookVerifier functionality.
 */
class WebhookVerifierTest {

    private static final String SECRET = "test-secret-key";
    private static final String VALID_PAYLOAD = "{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407}";

    @Test
    void testVerifyValidSignatureWithoutPrefix() throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = generateHmacSignature(SECRET, VALID_PAYLOAD);
        
        boolean result = WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, signature);
        
        assertTrue(result);
    }

    @Test
    void testVerifyValidSignatureWithPrefix() throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = "sha256=" + generateHmacSignature(SECRET, VALID_PAYLOAD);
        
        boolean result = WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, signature);
        
        assertTrue(result);
    }

    @Test
    void testVerifyInvalidSignature() {
        String invalidSignature = "invalid-signature";
        
        boolean result = WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, invalidSignature);
        
        assertFalse(result);
    }

    @Test
    void testVerifySignatureWithDifferentSecret() throws NoSuchAlgorithmException, InvalidKeyException {
        String wrongSecret = "wrong-secret";
        String wrongSignature = generateHmacSignature(wrongSecret, VALID_PAYLOAD);
        
        boolean result = WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, wrongSignature);
        
        assertFalse(result);
    }

    @Test
    void testVerifySignatureWithEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.verifySignature("", "payload", "signature"));
        assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.verifySignature("secret", "", "signature"));
        assertFalse(WebhookVerifier.verifySignature("secret", "payload", ""));
    }

    @Test
    void testVerifySignatureWithNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.verifySignature(null, "payload", "signature"));
        assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.verifySignature("secret", null, "signature"));
        assertFalse(WebhookVerifier.verifySignature("secret", "payload", null));
    }

    @Test
    void testGenerateSignatureWithoutPrefix() throws NoSuchAlgorithmException, InvalidKeyException {
        String expectedSignature = generateHmacSignature(SECRET, VALID_PAYLOAD);
        
        String generatedSignature = WebhookVerifier.generateSignature(SECRET, VALID_PAYLOAD);
        
        assertEquals(expectedSignature, generatedSignature);
    }

    @Test
    void testGenerateSignatureWithPrefix() throws NoSuchAlgorithmException, InvalidKeyException {
        String expectedSignature = "sha256=" + generateHmacSignature(SECRET, VALID_PAYLOAD);
        
        String generatedSignature = WebhookVerifier.generateSignature(SECRET, VALID_PAYLOAD, true);
        
        assertEquals(expectedSignature, generatedSignature);
    }

    @Test
    void testGenerateAndVerifySignatureRoundTrip() throws NoSuchAlgorithmException, InvalidKeyException {
        // Test without prefix
        String signature = WebhookVerifier.generateSignature(SECRET, VALID_PAYLOAD);
        assertTrue(WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, signature));
        
        // Test with prefix
        String signatureWithPrefix = WebhookVerifier.generateSignature(SECRET, VALID_PAYLOAD, true);
        assertTrue(WebhookVerifier.verifySignature(SECRET, VALID_PAYLOAD, signatureWithPrefix));
    }

    @Test
    void testParseSingleEventPayload() {
        String payload = "{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407,\"metadata\":{\"campaign_id\":\"test\"},\"tag\":\"newsletter\"}";

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);

        assertFalse(result.isBatch());
        assertEquals(1, result.getEvents().size());
        assertEquals("delivery", result.getEvents().get(0).get("event"));
        assertEquals("user@example.com", result.getEvents().get(0).get("email"));
    }

    @Test
    void testParseBatchEventPayload() {
        String payload = "[{\"event\":\"delivery\",\"email\":\"user1@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407},{\"event\":\"open\",\"email\":\"user2@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0b\",\"timestamp\":1753502500}]";

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);

        assertTrue(result.isBatch());
        assertEquals(2, result.getEvents().size());
        assertEquals("delivery", result.getEvents().get(0).get("event"));
        assertEquals("open", result.getEvents().get(1).get("event"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"request", "delivery", "open", "click", "drop", "spam_complaint", "unsubscribe", "bounce"})
    void testValidEventTypes(String eventType) {
        String payload = String.format("{\"event\":\"%s\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407}", eventType);

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);
        
        assertEquals(eventType, result.getEvents().get(0).get("event"));
    }

    @Test
    void testParseInvalidJsonPayload() {
        String invalidJson = "{\"event\":\"delivery\",\"email\"";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(invalidJson));
        
        assertTrue(exception.getMessage().contains("Invalid JSON payload"));
    }

    @Test
    void testParsePayloadMissingRequiredFields() {
        String payload = "{\"event\":\"delivery\"}"; // Missing required fields
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(payload));
        
        assertTrue(exception.getMessage().contains("Missing required field"));
    }

    @Test
    void testParsePayloadKeepsUnknownEventType() {
        String payload = "{\"event\":\"future_event_type\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407}";

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);

        assertEquals("future_event_type", result.getEvents().get(0).get("event"));
    }

    @Test
    void testParseBatchWithUnknownEventTypeKeepsEveryEvent() {
        String payload = "[{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"\",\"message_id\":\"H-1-a\",\"timestamp\":1753502407},{\"event\":\"future_event_type\",\"email\":\"user@example.com\",\"lane_id\":\"\",\"message_id\":\"H-1-b\",\"timestamp\":1753502408}]";

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);

        assertEquals(2, result.getEvents().size());
        assertEquals("delivery", result.getEvents().get(0).get("event"));
        assertEquals("future_event_type", result.getEvents().get(1).get("event"));
    }

    @Test
    void testParseRealRequestEventKeepsAllFields() {
        // Shape of a real request event: empty lane_id, id and mx_host at the top level.
        String payload = "{\"id\":\"01a136a7f4f77430913f16d9d3e195a8\",\"event\":\"request\",\"email\":\"user@example.com\",\"message_id\":\"H-0-01a136a7f4ef7bc2ac33bc34d2377fab-21183\",\"lane_id\":\"\",\"tag\":\"\",\"timestamp\":1791918339,\"mx_host\":\"aspmx.l.google.com\",\"metadata\":{\"run_id\":\"r1\"}}";

        Map<String, Object> event = WebhookVerifier.parseWebhookPayload(payload).getEvents().get(0);

        assertEquals("01a136a7f4f77430913f16d9d3e195a8", event.get("id"));
        assertEquals("", event.get("lane_id"));
        assertEquals("aspmx.l.google.com", event.get("mx_host"));
        assertEquals(Map.of("run_id", "r1"), event.get("metadata"));
    }

    @Test
    void testParseKeepsEventSpecificFields() {
        String payload = "{\"id\":\"8f14e45f-ceea-4e7a-b1c2-6d3f9a0b2c71\",\"event\":\"bounce\",\"email\":\"user@example.com\",\"message_id\":\"H-1-abc\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"timestamp\":1753502700,\"code\":\"550\",\"extended_code\":\"5.1.1\",\"is_hard\":true,\"classification\":\"Mailbox Unavailable\",\"tag\":\"welcome\",\"unsubscribe_group_id\":7}";

        Map<String, Object> event = WebhookVerifier.parseWebhookPayload(payload).getEvents().get(0);

        assertEquals("8f14e45f-ceea-4e7a-b1c2-6d3f9a0b2c71", event.get("id"));
        assertEquals("550", event.get("code"));
        assertEquals("5.1.1", event.get("extended_code"));
        assertEquals(true, event.get("is_hard"));
        assertEquals("Mailbox Unavailable", event.get("classification"));
        // Fields earlier versions returned as strings stay strings.
        assertEquals("1753502700", event.get("timestamp"));
        assertEquals("welcome", event.get("tag"));
        assertEquals("7", event.get("unsubscribe_group_id"));
    }

    @Test
    void testParsePayloadInvalidEmail() {
        String payload = "{\"event\":\"delivery\",\"email\":\"not-an-email\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407}";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(payload));
        
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not-a-uuid"})
    void testParsePayloadAcceptsAnyLaneId(String laneId) {
        String payload = "{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"" + laneId + "\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":1753502407}";

        WebhookVerifier.WebhookData result = WebhookVerifier.parseWebhookPayload(payload);

        assertEquals(laneId, result.getEvents().get(0).get("lane_id"));
    }

    @Test
    void testParsePayloadInvalidTimestamp() {
        String payload = "{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"timestamp\":\"not-a-timestamp\"}";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(payload));
        
        assertTrue(exception.getMessage().contains("Invalid timestamp format"));
    }

    @Test
    void testParseEmptyPayload() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(""));
        
        assertTrue(exception.getMessage().contains("Payload cannot be empty"));
    }

    @Test
    void testParseNullPayload() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            WebhookVerifier.parseWebhookPayload(null));
        
        assertTrue(exception.getMessage().contains("Payload cannot be empty"));
    }

    @Test
    void testGetSignatureHeaderName() {
        assertEquals("x-webhook-signature", WebhookVerifier.getSignatureHeaderName());
    }

    @Test
    void testExtractSignatureFromHeaders() {
        Map<String, String> headers = new HashMap<>();
        
        // Test standard header
        headers.put("x-webhook-signature", "sha256=abc123");
        assertEquals("sha256=abc123", WebhookVerifier.extractSignatureFromHeaders(headers));
        
        // Test uppercase header
        headers.clear();
        headers.put("X_WEBHOOK_SIGNATURE", "sha256=def456");
        assertEquals("sha256=def456", WebhookVerifier.extractSignatureFromHeaders(headers));
        
        // Test HTTP_ prefixed header
        headers.clear();
        headers.put("HTTP_X_WEBHOOK_SIGNATURE", "sha256=ghi789");
        assertEquals("sha256=ghi789", WebhookVerifier.extractSignatureFromHeaders(headers));
        
        // Test missing header
        headers.clear();
        headers.put("other-header", "value");
        assertNull(WebhookVerifier.extractSignatureFromHeaders(headers));
        
        // Test null headers
        assertNull(WebhookVerifier.extractSignatureFromHeaders(null));
    }

    @Test
    void testCompleteWebhookVerificationWorkflow() throws NoSuchAlgorithmException, InvalidKeyException {
        // Test data from documentation examples
        String eventData = "{\"event\":\"delivery\",\"email\":\"user@example.com\",\"lane_id\":\"5805dd85-ed8c-44db-91a7-1d53a41c86a5\",\"message_id\":\"H-1-019844e340027d728a7cfda632e14d0a\",\"metadata\":{\"campaign_id\":\"camp_456\",\"user_id\":\"user_123\"},\"tag\":\"newsletter-campaign\",\"timestamp\":1753502407}";

        String signature = WebhookVerifier.generateSignature(SECRET, eventData, true);

        // Simulate HTTP headers
        Map<String, String> headers = new HashMap<>();
        headers.put("x-webhook-signature", signature);

        // Step 1: Extract signature from headers
        String extractedSignature = WebhookVerifier.extractSignatureFromHeaders(headers);
        assertNotNull(extractedSignature);

        // Step 2: Verify signature
        assertTrue(WebhookVerifier.verifySignature(SECRET, eventData, extractedSignature));

        // Step 3: Parse payload
        WebhookVerifier.WebhookData parsed = WebhookVerifier.parseWebhookPayload(eventData);
        assertFalse(parsed.isBatch());
        assertEquals(1, parsed.getEvents().size());
        assertEquals("delivery", parsed.getEvents().get(0).get("event"));
        assertEquals("user@example.com", parsed.getEvents().get(0).get("email"));
    }

    @Test
    void testWebhookDataToString() {
        Map<String, Object> event = new HashMap<>();
        event.put("event", "delivery");
        event.put("email", "test@example.com");
        
        WebhookVerifier.WebhookData data = new WebhookVerifier.WebhookData(false, List.of(event));
        
        String result = data.toString();
        assertTrue(result.contains("WebhookData"));
        assertTrue(result.contains("isBatch=false"));
        assertTrue(result.contains("events="));
    }

    // Helper method to generate HMAC signature for testing
    private String generateHmacSignature(String secret, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
