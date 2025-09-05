# Laneful Java SDK Example

This is a standalone example project that demonstrates how to use the published Laneful Java SDK from Maven Central.

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- A valid Laneful account with API access

## Setup

1. **Get your API credentials** from your Laneful dashboard:

   - Base URL (e.g., `https://your-endpoint.send.laneful.net`)
   - Auth Token

2. **Update the credentials** in `src/main/java/com/laneful/example/SimpleExample.java`:
   ```java
   String baseUrl = "https://your-endpoint.send.laneful.net"; // Replace with your actual endpoint
   String authToken = "your-auth-token"; // Replace with your actual auth token
   ```

## Running the Example

### Option 1: Using Maven Exec Plugin

```bash
mvn compile exec:java
```

### Option 2: Compile and Run Manually

```bash
# Compile the project
mvn compile

# Run the example
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.laneful.example.SimpleExample
```

## What This Example Demonstrates

The example includes 6 different scenarios:

1. **Simple Text Email** - Basic email sending with text content
2. **HTML Email with Tracking** - Rich HTML content with click/open tracking
3. **Template Email** - Using pre-defined templates with dynamic data
4. **Multiple Recipients** - Sending to multiple TO, CC, and BCC recipients
5. **Scheduled Email** - Scheduling emails for future delivery
6. **Batch Emails** - Sending multiple emails in a single request

## Expected Output

When running successfully, you should see output like:

```
✓ Laneful client created successfully

📧 Example 1: Sending simple text email...
✓ Simple email sent successfully!
  Response: {messageId=abc123, status=sent}

📧 Example 2: Sending HTML email with tracking...
✓ HTML email sent successfully!
  Response: {messageId=def456, status=sent}

... (and so on for all examples)

🎉 All examples completed successfully!
The Laneful Java SDK is working correctly with the published library from Maven Central!
```

## Troubleshooting

### Common Issues

1. **"Failed to create client"** - Check your `baseUrl` and `authToken`
2. **"API error"** - Verify your credentials and endpoint URL
3. **"HTTP error"** - Check your network connection
4. **"Validation error"** - Ensure email addresses and content are properly formatted

### Getting Help

- Review the [Laneful Java SDK Source](https://github.com/your-org/laneful-java)
- Contact support through your Laneful dashboard

## Dependencies

This example uses:

- **Laneful Java SDK** (1.0.0) - The main library from Maven Central
- **SLF4J Simple** (2.0.9) - For logging output

All dependencies are automatically resolved by Maven from Maven Central.
