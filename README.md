# Laneful Java Client

A Java client library for the Laneful email API.

## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.laneful</groupId>
    <artifactId>laneful-java</artifactId>
    <version>1.2.0</version>
</dependency>
```

### Building from Source

```bash
git clone https://github.com/lanefulhq/laneful-java.git
cd laneful-java
mvn clean install
```

## Quick Start

```java
import com.laneful.client.LanefulClient;
import com.laneful.models.Email;
import com.laneful.models.Address;

// Create client
LanefulClient client = new LanefulClient(
    "https://your-endpoint.send.laneful.net",
    "your-auth-token"
);

// Create email
Email email = new Email.Builder()
    .from(new Address("sender@example.com", "Your Name"))
    .to(new Address("recipient@example.com", "Recipient Name"))
    .subject("Hello from Laneful")
    .textContent("This is a test email.")
    .htmlContent("<h1>This is a test email.</h1>")
    .build();

// Send email
try {
    Map<String, Object> response = client.sendEmail(email);
    System.out.println("Email sent successfully");
} catch (Exception e) {
    System.err.println("Failed to send email: " + e.getMessage());
}
```

## Examples

The SDK includes comprehensive examples demonstrating all features. Check the `src/main/java/com/laneful/examples/` directory for available examples.

To run any example:

```bash
# Compile and run an example
mvn compile exec:java -Dexec.mainClass="com.laneful.examples.ExampleName"
```

## Features

- Send single or multiple emails
- Plain text and HTML content
- Email templates with dynamic data
- File attachments
- Email tracking (opens, clicks, unsubscribes)
- Custom headers and reply-to addresses
- Visible `from_header` and request-level `mail_settings`
- Scheduled sending
- Webhook signature verification
- Domain management (list, create, verify, update email track, delete)
- Unsubscribe groups
- Deliverability analytics (spam-ratio radar, Google Postmaster, Microsoft SNDS)

## Examples

### Template Email

```java
Email email = new Email.Builder()
    .from(new Address("sender@example.com"))
    .to(new Address("user@example.com"))
    .templateId("welcome-template")
    .templateData(Map.of(
        "name", "John Doe",
        "company", "Acme Corp"
    ))
    .build();

Map<String, Object> response = client.sendEmail(email);
```

### Email with Attachments

```java
import com.laneful.models.Attachment;
import java.nio.file.Paths;

// Create attachment from file
Attachment attachment = Attachment.fromFile(Paths.get("/path/to/document.pdf"));

Email email = new Email.Builder()
    .from(new Address("sender@example.com"))
    .to(new Address("user@example.com"))
    .subject("Document Attached")
    .textContent("Please find the document attached.")
    .attachment(attachment)
    .build();

Map<String, Object> response = client.sendEmail(email);
```

### Email with Tracking

```java
import com.laneful.models.TrackingSettings;

TrackingSettings tracking = new TrackingSettings(true, true, true);

Email email = new Email.Builder()
    .from(new Address("sender@example.com"))
    .to(new Address("user@example.com"))
    .subject("Tracked Email")
    .htmlContent("<p>This email is tracked.</p>")
    .tracking(tracking)
    .build();

Map<String, Object> response = client.sendEmail(email);
```

### Multiple Recipients

```java
Email email = new Email.Builder()
    .from(new Address("sender@example.com"))
    .to(new Address("user1@example.com"))
    .to(new Address("user2@example.com", "User Two"))
    .cc(new Address("cc@example.com"))
    .bcc(new Address("bcc@example.com"))
    .subject("Multiple Recipients")
    .textContent("This email has multiple recipients.")
    .build();

Map<String, Object> response = client.sendEmail(email);
```

### Scheduled Email

```java
import java.time.Instant;

// Schedule for 24 hours from now
long sendTime = Instant.now().plusSeconds(24 * 60 * 60).getEpochSecond();

Email email = new Email.Builder()
    .from(new Address("sender@example.com"))
    .to(new Address("user@example.com"))
    .subject("Scheduled Email")
    .textContent("This email was scheduled.")
    .sendTime(sendTime)
    .build();

Map<String, Object> response = client.sendEmail(email);
```

### Multiple Emails

```java
List<Email> emails = List.of(
    new Email.Builder()
        .from(new Address("sender@example.com"))
        .to(new Address("user1@example.com"))
        .subject("Email 1")
        .textContent("First email content.")
        .build(),
    new Email.Builder()
        .from(new Address("sender@example.com"))
        .to(new Address("user2@example.com"))
        .subject("Email 2")
        .textContent("Second email content.")
        .build()
);

Map<String, Object> response = client.sendEmails(emails);
```

### Visible From header

```java
Email email = new Email.Builder()
    .from(new Address("sender@example.com", "Your Name"))
    .to(new Address("user@example.com"))
    .subject("Hello")
    .textContent("Hello")
    .fromHeader(new Address("newsletter@example.com", "Newsletter"))
    .build();
```

### Mail settings (sandbox and message IDs)

```java
Map<String, Object> response = client.sendEmail(
    email,
    new MailSettings(true, true)
);
// response.get("message_ids") is present when returnMessageIds is true
```

### Tracking with an unsubscribe group

```java
TrackingSettings tracking = new TrackingSettings(
    true,
    true,
    true,
    123L,
    // ignored if unsubscribeGroupId is set
    "Newsletters"
);
```

### Custom Timeout

```java
import java.time.Duration;

LanefulClient client = new LanefulClient(
    "https://your-endpoint.send.laneful.net",
    "your-auth-token",
    Duration.ofSeconds(60) // 60 second timeout
);
```

## Webhook Verification

The Java SDK provides comprehensive webhook handling with signature verification, payload parsing, and validation.

### Basic Signature Verification

```java
import com.laneful.webhooks.WebhookVerifier;

// In your webhook handler
String payload = request.getBody(); // Get the raw request body
String signature = request.getHeader("x-webhook-signature");
String secret = "your-webhook-secret";

if (WebhookVerifier.verifySignature(secret, payload, signature)) {
    // Process webhook data
    WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(payload);
    // Handle webhook events
} else {
    // Invalid signature
    response.setStatus(401);
}
```

### Advanced Webhook Processing

```java
import com.laneful.webhooks.WebhookVerifier;
import java.util.Map;

// Complete webhook verification and processing workflow
try {
    // Step 1: Get raw payload
    String payload = request.getBody();
    
    // Step 2: Extract signature from headers (supports multiple formats)
    String signature = WebhookVerifier.extractSignatureFromHeaders(getHeaders(request));
    
    // Step 3: Verify signature (supports sha256= prefix)
    if (!WebhookVerifier.verifySignature(webhookSecret, payload, signature)) {
        throw new SecurityException("Invalid webhook signature");
    }
    
    // Step 4: Parse and validate payload structure
    WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(payload);
    
    // Step 5: Process events (handles both batch and single event formats)
    for (Map<String, Object> event : webhookData.getEvents()) {
        String eventType = (String) event.get("event");
        String email = (String) event.get("email");
        
        switch (eventType) {
            case "request":
                handleRequestEvent(event);
                break;
            case "delivery":
                handleDeliveryEvent(event);
                break;
            case "open":
                handleOpenEvent(event);
                break;
            case "click":
                handleClickEvent(event);
                break;
            // ... handle other event types
        }
    }
    
} catch (IllegalArgumentException e) {
    // Payload validation error
    response.setStatus(400);
} catch (Exception e) {
    // Other errors
    response.setStatus(401);
}
```

### Supported Event Types

- `request` - Send request accepted
- `delivery` - Email delivered successfully
- `open` - Email opened by recipient
- `click` - Link clicked in email
- `bounce` - Email bounced (hard or soft)
- `drop` - Email dropped (spam, invalid, etc.)
- `spam_complaint` - Recipient marked email as spam
- `unsubscribe` - Recipient unsubscribed

### Batch Mode Support

The webhook handler automatically detects and processes both single events and batch events:

```java
WebhookVerifier.WebhookData webhookData = WebhookVerifier.parseWebhookPayload(payload);

if (webhookData.isBatch()) {
    // Processing multiple events in batch mode
    System.out.println("Processing " + webhookData.getEvents().size() + " events in batch");
} else {
    // Processing single event
    System.out.println("Processing single event");
}
```

## Error Handling

```java
import com.laneful.exceptions.*;

try {
    Map<String, Object> response = client.sendEmail(email);
    System.out.println("Email sent successfully");
} catch (ValidationException e) {
    // Invalid input data
    System.err.println("Validation error: " + e.getMessage());
} catch (ApiException e) {
    // API returned an error
    System.err.println("API error: " + e.getMessage());
    System.err.println("Status code: " + e.getStatusCode());
    System.err.println("Error message: " + e.getErrorMessage());
} catch (HttpException e) {
    // Network or HTTP-level error
    System.err.println("HTTP error: " + e.getMessage());
    System.err.println("Status code: " + e.getStatusCode());
} catch (Exception e) {
    // Other unexpected errors
    System.err.println("Unexpected error: " + e.getMessage());
}
```

## API Reference

### LanefulClient

#### Constructors

- `LanefulClient(String baseUrl, String authToken)` - Creates client with default timeout (30 seconds)
- `LanefulClient(String baseUrl, String authToken, Duration timeout)` - Creates client with custom timeout
- `LanefulClient(String baseUrl, String authToken, Duration timeout, OkHttpClient httpClient)` - Creates client with custom HTTP client

#### Methods

Send host (`https://your-endpoint.send.laneful.net`):

- `Map<String, Object> sendEmail(Email email)` / `sendEmail(Email email, MailSettings settings)`
- `Map<String, Object> sendEmails(List<Email> emails)` / `sendEmails(List<Email> emails, MailSettings settings)`

Organization API host (`https://api.laneful.net`):

- `ListUnsubscribeGroupsResponse listUnsubscribeGroups(long workspaceId, ListUnsubscribeGroupsParams params)`
- `UnsubscribeGroup createUnsubscribeGroup(long workspaceId, String name)`
- `UnsubscribeGroup updateUnsubscribeGroup(long workspaceId, long unsubscribeGroupId, String name)`
- `ListDomainsResponse listDomains(long workspaceId, ListDomainsParams params)`
- `Domain getDomain(long workspaceId, String domain)`
- `Domain createDomain(long workspaceId, CreateDomainRequest request)`
- `Domain updateDomain(long workspaceId, String domain, UpdateDomainRequest request)`
- `Domain verifyDomain(long workspaceId, String domain)`
- `SuccessResponse deleteDomain(long workspaceId, String domain)`
- `ListDomainSpamRatioRadarResponse listDomainSpamRatioRadar(ListDomainSpamRatioRadarParams params)`
- `ListGooglePostmasterSpamReportsResponse listGooglePostmasterSpamReports(ListGooglePostmasterSpamReportsParams params)`
- `ListSndsReportsResponse listSndsReports(ListSndsReportsParams params)`

### Email.Builder

#### Required Fields

- `from(Address from)` - Sender address

#### Optional Fields

- `to(Address to)` / `to(String email)` / `to(String email, String name)` - Recipient addresses
- `cc(Address cc)` / `cc(String email)` / `cc(String email, String name)` - CC addresses
- `bcc(Address bcc)` / `bcc(String email)` / `bcc(String email, String name)` - BCC addresses
- `subject(String subject)` - Email subject
- `textContent(String textContent)` - Plain text content
- `htmlContent(String htmlContent)` - HTML content
- `templateId(String templateId)` - Template ID
- `templateData(Map<String, Object> templateData)` - Template data
- `attachment(Attachment attachment)` - File attachments
- `headers(Map<String, String> headers)` - Custom headers
- `replyTo(Address replyTo)` / `replyTo(String email)` / `replyTo(String email, String name)` - Reply-to address
- `sendTime(Long sendTime)` - Scheduled send time (Unix timestamp)
- `webhookData(Map<String, String> webhookData)` - Webhook data
- `tag(String tag)` - Email tag
- `tracking(TrackingSettings tracking)` - Tracking settings
- `fromHeader(Address fromHeader)` / `fromHeader(String email)` / `fromHeader(String email, String name)` - Visible From header

### Address

- `Address(String email)` - Creates address with email only
- `Address(String email, String name)` - Creates address with email and name

### Attachment

- `Attachment.fromFile(File file)` - Creates attachment from file
- `Attachment.fromFile(Path path)` - Creates attachment from file path
- `Attachment(String filename, String contentType, String content)` - Creates attachment from raw data

### TrackingSettings

- `TrackingSettings(boolean opens, boolean clicks, boolean unsubscribes)` - Creates tracking settings
- `TrackingSettings(boolean opens, boolean clicks, boolean unsubscribes, Long unsubscribeGroupId, String unsubscribeGroupName)` - Tracking with an unsubscribe group

### MailSettings

- `MailSettings(Boolean sandboxMode, Boolean returnMessageIds)` - Request-level send options

## Domain, unsubscribe groups, and analytics

These endpoints live on the organization API host. Point the client at it:

```java
LanefulClient client = new LanefulClient(
    "https://api.laneful.net",
    "your-auth-token"
);
```

### Unsubscribe groups

```java
var groups = client.listUnsubscribeGroups(42, new ListUnsubscribeGroupsParams(null, 50, null));
var created = client.createUnsubscribeGroup(42, "Newsletters");
var updated = client.updateUnsubscribeGroup(42, created.unsubscribeGroupId(), "Weekly Newsletters");
```

### Domains

```java
var list = client.listDomains(42, new ListDomainsParams(null, 50, null));
Domain domain = client.createDomain(42, new CreateDomainRequest("mydomain.com", "tracking", "return-path"));
domain = client.getDomain(42, "mydomain.com");
domain = client.verifyDomain(42, "mydomain.com");

// Set the email track; pass "" to clear it, or omit emailTrackId to leave it unchanged
domain = client.updateDomain(42, "mydomain.com", new UpdateDomainRequest("e59f0a35-05bc-4516-b585-c06f69c3e67e"));

client.deleteDomain(42, "mydomain.com");
```

### Deliverability analytics

```java
var radar = client.listDomainSpamRatioRadar(new ListDomainSpamRatioRadarParams(
    List.of(1L, 2L),
    "example.com",
    "2026-09-01",
    "2026-09-08",
    null,
    null
));

var postmaster = client.listGooglePostmasterSpamReports(
    new ListGooglePostmasterSpamReportsParams(List.of(), "example.com", null, null, null, null)
);

var snds = client.listSndsReports(new ListSndsReportsParams("203.0.113.5", null, null, null, null));
```

### WebhookVerifier

#### Signature Verification
- `boolean verifySignature(String secret, String payload, String signature)` - Verifies webhook signature (supports sha256= prefix)
- `String generateSignature(String secret, String payload)` - Generates signature for payload
- `String generateSignature(String secret, String payload, boolean includePrefix)` - Generates signature with optional prefix

#### Payload Processing
- `WebhookData parseWebhookPayload(String payload)` - Parse and validate webhook payload structure
- `String getSignatureHeaderName()` - Get the correct header name for webhook signatures
- `String extractSignatureFromHeaders(Map<String, String> headers)` - Extract signature from HTTP headers

#### WebhookData
- `boolean isBatch()` - Returns true if payload contains multiple events
- `List<Map<String, Object>> getEvents()` - Returns list of parsed events

## Exception Types

- `ValidationException` - Thrown when input validation fails
- `ApiException` - Thrown when the API returns an error response
- `HttpException` - Thrown when HTTP communication fails
- `LanefulException` - Base exception class for all SDK exceptions

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
