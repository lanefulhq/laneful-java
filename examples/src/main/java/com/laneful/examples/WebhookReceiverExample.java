package com.laneful.examples;

import com.laneful.webhooks.WebhookVerifier;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Receives Laneful webhooks: verifies the signature, then processes single or batch events.
 */
public class WebhookReceiverExample {
    public static void main(String[] args) throws IOException {
        String webhookSecret = System.getenv("LANEFUL_WEBHOOK_SECRET");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));

        if (webhookSecret == null || webhookSecret.isEmpty()) {
            System.err.println("❌ Missing required environment variable: LANEFUL_WEBHOOK_SECRET");
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> handle(exchange, webhookSecret));
        server.start();
        System.out.println("Webhook example listening on http://localhost:" + port + "/");
    }

    private static void handle(HttpExchange exchange, String webhookSecret) throws IOException {
        // Verify the raw body: re-serializing parsed JSON can change the bytes.
        String payload = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String signature = exchange.getRequestHeaders().getFirst(WebhookVerifier.getSignatureHeaderName());

        if (signature == null || payload.isEmpty()
                || !WebhookVerifier.verifySignature(webhookSecret, payload, signature)) {
            respond(exchange, 401, "Invalid signature");
            return;
        }

        try {
            WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(payload);
            for (Map<String, Object> event : webhookData.getEvents()) {
                System.out.printf("[%s] id=%s message_id=%s email=%s tag=%s metadata=%s event=%s%n",
                    event.get("event"), event.get("id"), event.get("message_id"), event.get("email"),
                    event.get("tag"), event.get("metadata"), event);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid webhook payload: " + e.getMessage());
            respond(exchange, 400, "Invalid payload");
            return;
        }

        respond(exchange, 200, "OK");
    }

    private static void respond(HttpExchange exchange, int status, String message) throws IOException {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
