package com.laneful.webhooks;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for verifying webhook signatures.
 */
public class WebhookVerifier {
    
    private static final String ALGORITHM = "HmacSHA256";
    
    /**
     * Verifies a webhook signature.
     * 
     * @param secret The webhook secret
     * @param payload The webhook payload
     * @param signature The signature to verify
     * @return true if the signature is valid, false otherwise
     */
    public static boolean verifySignature(String secret, String payload, String signature) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret cannot be empty");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        if (signature == null || signature.trim().isEmpty()) {
            return false;
        }
        
        try {
            String expectedSignature = generateSignature(secret, payload);
            return constantTimeEquals(expectedSignature, signature);
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
}
