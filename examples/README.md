# Laneful Java SDK Examples

This directory contains comprehensive examples that match the web-admin Java SDK documentation. These examples demonstrate all the features and use cases shown in the official documentation.

## Prerequisites

1. **Docker** - For running examples in a containerized environment
2. **Laneful Account** - You need a valid Laneful account with API access
3. **API Credentials** - Your Laneful API credentials (see Environment Variables section)

## Quick Start with Docker

The easiest way to run the examples is using Docker. This ensures a consistent environment and handles all dependencies automatically.

### 1. Set up Environment Variables

Create a `.env` file in the `examples` directory with your Laneful credentials:

```bash
# Required: Your Laneful API credentials
LANEFUL_BASE_URL=https://your-endpoint.send.laneful.net
LANEFUL_AUTH_TOKEN=your-auth-token-here
LANEFUL_FROM_EMAIL=your-email@yourdomain.com

# Required: Test recipient emails (comma-separated)
LANEFUL_TO_EMAILS=test1@example.com,test2@example.com

# Optional: Template and webhook settings
LANEFUL_TEMPLATE_ID=6
LANEFUL_WEBHOOK_SECRET=your-webhook-secret
LANEFUL_WEBHOOK_URL=https://yourdomain.com/webhook
```

### 2. Build the Docker Image

From the `laneful-java` root directory:

```bash
docker build --no-cache -f examples/Dockerfile -t laneful-java-examples:latest .
```

### 3. Run Examples

Run any example using Docker:

```bash
# Basic email example
docker run --rm --env-file examples/.env laneful-java-examples:latest BasicEmailExample

# Template email example
docker run --rm --env-file examples/.env laneful-java-examples:latest TemplateEmailExample

# HTML email with tracking
docker run --rm --env-file examples/.env laneful-java-examples:latest HTMLEmailWithTrackingExample
```

## Local Development Setup

If you prefer to run examples locally without Docker:

1. **Java 21+** - The SDK requires Java 21 or higher
2. **Maven 3.6+** - For building and running examples

### Build and Run Locally

```bash
# Build the project
mvn clean compile

# Run examples (set environment variables first)
export LANEFUL_BASE_URL="https://your-endpoint.send.laneful.net"
export LANEFUL_AUTH_TOKEN="your-auth-token"
export LANEFUL_FROM_EMAIL="your-email@yourdomain.com"
export LANEFUL_TO_EMAILS="test1@example.com,test2@example.com"

# Run an example
mvn exec:java -Dexec.mainClass="com.laneful.examples.BasicEmailExample"
```

## Examples

### Basic Examples

| Example | Description | Docker Command | Local Command |
|---------|-------------|----------------|---------------|
| `BasicEmailExample` | Simple text email sending | `docker run --rm --env-file .env laneful-java-examples:latest BasicEmailExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.BasicEmailExample"` |
| `HTMLEmailWithTrackingExample` | HTML email with tracking enabled | `docker run --rm --env-file .env laneful-java-examples:latest HTMLEmailWithTrackingExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.HTMLEmailWithTrackingExample"` |
| `TemplateEmailExample` | Template-based email sending | `docker run --rm --env-file .env laneful-java-examples:latest TemplateEmailExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.TemplateEmailExample"` |
| `AttachmentEmailExample` | Email with file attachments | `docker run --rm --env-file .env laneful-java-examples:latest AttachmentEmailExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.AttachmentEmailExample"` |
| `MultipleRecipientsExample` | Email to multiple recipients with CC/BCC | `docker run --rm --env-file .env laneful-java-examples:latest MultipleRecipientsExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.MultipleRecipientsExample"` |
| `ScheduledEmailExample` | Scheduled email delivery | `docker run --rm --env-file .env laneful-java-examples:latest ScheduledEmailExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.ScheduledEmailExample"` |
| `BatchEmailExample` | Sending multiple emails in batch | `docker run --rm --env-file .env laneful-java-examples:latest BatchEmailExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.BatchEmailExample"` |

### Advanced Examples

| Example | Description | Docker Command | Local Command |
|---------|-------------|----------------|---------------|
| `ErrorHandlingExample` | Comprehensive error handling | `docker run --rm --env-file .env laneful-java-examples:latest ErrorHandlingExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.ErrorHandlingExample"` |
| `WebhookHandlerExample` | Webhook signature verification and processing | `docker run --rm --env-file .env laneful-java-examples:latest WebhookHandlerExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.WebhookHandlerExample"` |
| `ComprehensiveExample` | All features in one example | `docker run --rm --env-file .env laneful-java-examples:latest ComprehensiveExample` | `mvn exec:java -Dexec.mainClass="com.laneful.examples.ComprehensiveExample"` |

## Features Demonstrated

### ✅ Email Sending
- **Basic Text Emails** - Simple text content
- **HTML Emails** - Rich HTML content with fallback text
- **Template Emails** - Dynamic content using templates
- **Attachments** - File attachments with automatic base64 encoding
- **Multiple Recipients** - TO, CC, BCC, and Reply-To support
- **Scheduled Delivery** - Send emails at specific times
- **Batch Sending** - Send multiple emails efficiently

### ✅ Tracking & Analytics
- **Open Tracking** - Track when emails are opened
- **Click Tracking** - Track link clicks in emails
- **Unsubscribe Tracking** - Handle unsubscribe events
- **Custom Tags** - Categorize emails for analytics

### ✅ Webhook Handling
- **Signature Verification** - HMAC-SHA256 signature validation
- **Event Processing** - Handle all webhook event types
- **Batch Mode Support** - Process multiple events in one webhook
- **Error Handling** - Comprehensive error handling and logging

### ✅ Error Handling
- **ValidationException** - Input validation errors
- **ApiException** - API-level errors with status codes
- **HttpException** - Network and HTTP errors
- **Comprehensive Logging** - Detailed error information

## Webhook Events Supported

The examples handle all documented webhook events:

| Event Type | Description |
|------------|-------------|
| `delivery` | Email delivered successfully |
| `open` | Email opened by recipient |
| `click` | Link clicked in email |
| `bounce` | Email bounced (hard or soft) |
| `drop` | Email dropped (spam, invalid, etc.) |
| `spam_complaint` | Recipient marked email as spam |
| `unsubscribe` | Recipient unsubscribed |

## Integration Examples

### Spring Boot Integration

```java
@RestController
public class WebhookController {
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader Map<String, String> headers) {
        
        try {
            WebhookHandlerExample handler = new WebhookHandlerExample();
            Map<String, Object> response = handler.handleWebhook(payload, headers);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
}
```

### Servlet Integration

```java
@WebServlet("/webhook")
public class WebhookServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Use the WebhookHandlerExample logic here
        // See WebhookHandlerExample.java for complete implementation
    }
}
```

## Testing

### Unit Testing

```java
@Test
public void testEmailCreation() throws ValidationException {
    Email email = new Email.Builder()
        .from(new Address("test@example.com"))
        .to(new Address("recipient@example.com"))
        .subject("Test Email")
        .textContent("Test content")
        .build();
    
    assertNotNull(email);
    assertEquals("test@example.com", email.getFrom().email());
}
```

### Integration Testing

```java
@Test
public void testEmailSending() throws Exception {
    LanefulClient client = new LanefulClient(baseUrl, authToken);
    Email email = createTestEmail();
    
    Map<String, Object> response = client.sendEmail(email);
    
    assertNotNull(response);
    assertTrue(response.containsKey("status"));
}
```

## Environment Variables

All examples use environment variables for configuration. Here's the complete list:

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `LANEFUL_BASE_URL` | Your Laneful API endpoint | `https://your-endpoint.send.laneful.net` |
| `LANEFUL_AUTH_TOKEN` | Your API authentication token | `priv-xxxxxxxxxxxxxxxx` |
| `LANEFUL_FROM_EMAIL` | Email address to send from | `noreply@yourdomain.com` |
| `LANEFUL_TO_EMAILS` | Comma-separated recipient emails | `test1@example.com,test2@example.com` |

### Optional Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `LANEFUL_TEMPLATE_ID` | Template ID for template examples | `6` |
| `LANEFUL_WEBHOOK_SECRET` | Webhook signature verification secret | `your-webhook-secret` |
| `LANEFUL_WEBHOOK_URL` | Your webhook endpoint URL | `https://yourdomain.com/webhook` |

## Troubleshooting

### Common Issues

1. **ValidationException**: Check your email addresses and required fields
2. **ApiException**: Verify your API credentials and endpoint URL
3. **HttpException**: Check your network connection and firewall settings
4. **File Not Found**: Ensure attachment files exist at the specified paths
5. **Docker Build Fails**: Make sure you're running the build command from the `laneful-java` root directory
6. **Environment Variables Not Found**: Ensure your `.env` file is in the `examples` directory and contains all required variables

### Docker Troubleshooting

```bash
# Check if Docker image was built successfully
docker images | grep laneful-java-examples

# Run container interactively for debugging
docker run --rm -it --env-file .env --entrypoint /bin/bash laneful-java-examples:latest

# Check container logs
docker run --rm --env-file .env laneful-java-examples:latest BasicEmailExample 2>&1 | tee output.log
```

### Debug Mode

For local development, enable debug logging:

```bash
export JAVA_OPTS="-Djava.util.logging.config.file=logging.properties"
mvn exec:java -Dexec.mainClass="com.laneful.examples.BasicEmailExample"
```

## Documentation

- **Web Admin Documentation**: [Java SDK Docs](https://app.laneful.com/docs/java-sdk)
- **API Reference**: [Laneful API Docs](https://app.laneful.com/docs/api)
- **GitHub Repository**: [laneful-java](https://github.com/lanefulhq/laneful-java)

## Support

- **Issues**: [GitHub Issues](https://github.com/lanefulhq/laneful-java/issues)
- **Documentation**: [Laneful Docs](https://app.laneful.com/docs)
- **Email**: support@laneful.com

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.
