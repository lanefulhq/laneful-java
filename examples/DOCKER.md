# 🐳 Docker Setup for Laneful Java SDK Examples

This guide shows you how to run the Laneful Java SDK examples in Docker with real credentials.

## 🚀 Quick Start

### 1. Setup Environment

```bash
# Copy the example environment file
cp env.example .env

# Edit .env with your real credentials
nano .env
```

### 2. Build and Run

```bash
# Make the script executable
chmod +x run-docker.sh

# Build the Docker image
./run-docker.sh build

# Run a specific example
./run-docker.sh BasicEmailExample
```

## 📋 Available Commands

### Using the Helper Script

```bash
# Build Docker image
./run-docker.sh build

# Run specific example
./run-docker.sh BasicEmailExample
./run-docker.sh HTMLEmailWithTrackingExample
./run-docker.sh ComprehensiveExample

# Open shell in container
./run-docker.sh shell

# Run with docker-compose
./run-docker.sh compose

# Clean up Docker resources
./run-docker.sh clean

# Show help
./run-docker.sh help
```

### Using Docker Directly

```bash
# Build image
docker build -t laneful-java-examples:latest .

# Run example with environment file
docker run --rm --env-file .env laneful-java-examples:latest BasicEmailExample

# Run with interactive shell
docker run --rm -it --env-file .env laneful-java-examples:latest /bin/bash
```

### Using Docker Compose

```bash
# Run default service (BasicEmailExample)
docker-compose up --build

# Run specific service
docker-compose up --build webhook-handler

# Run in background
docker-compose up -d

# Stop services
docker-compose down
```

## 🔧 Configuration

### Environment Variables

The following environment variables are used:

| Variable | Description | Example |
|----------|-------------|---------|
| `LANEFUL_BASE_URL` | Your Laneful API endpoint | `https://nanmhr7rwaq3nbzo.z1.send.dev.laneful.net` |
| `LANEFUL_AUTH_TOKEN` | Your API authentication token | `priv-CvxChceENYcxr0AGnI7CF` |
| `LANEFUL_FROM_EMAIL` | Email address to send from | `alex@1116291.site` |
| `LANEFUL_TO_EMAILS` | Comma-separated recipient emails | `alex@joylabsventures.com,herrera.luis@uabc.edu.mx` |
| `LANEFUL_WEBHOOK_SECRET` | Webhook HMAC secret | `8a4db6a190a34f2a4cc9bfcfc2ba96894abc6505856b6153f38724c137958d81` |
| `LANEFUL_WEBHOOK_URL` | Your webhook endpoint URL | `https://1116291.site/webhook` |

### .env File

Create a `.env` file with your real credentials:

```bash
# Copy from template
cp env.example .env

# Edit with your values
nano .env
```

**⚠️ Important**: The `.env` file is gitignored and will not be committed to version control.

## 📧 Available Examples

| Example | Description | Command |
|---------|-------------|---------|
| `BasicEmailExample` | Simple text email | `./run-docker.sh BasicEmailExample` |
| `HTMLEmailWithTrackingExample` | HTML email with tracking | `./run-docker.sh HTMLEmailWithTrackingExample` |
| `TemplateEmailExample` | Template-based email | `./run-docker.sh TemplateEmailExample` |
| `AttachmentEmailExample` | Email with attachments | `./run-docker.sh AttachmentEmailExample` |
| `MultipleRecipientsExample` | Multiple recipients | `./run-docker.sh MultipleRecipientsExample` |
| `ScheduledEmailExample` | Scheduled email | `./run-docker.sh ScheduledEmailExample` |
| `BatchEmailExample` | Batch email sending | `./run-docker.sh BatchEmailExample` |
| `ErrorHandlingExample` | Error handling demo | `./run-docker.sh ErrorHandlingExample` |
| `WebhookHandlerExample` | Webhook processing | `./run-docker.sh WebhookHandlerExample` |
| `ComprehensiveExample` | All features combined | `./run-docker.sh ComprehensiveExample` |

## 🐳 Docker Services

### Main Service (`laneful-java-examples`)

Runs individual examples with real credentials.

```yaml
services:
  laneful-java-examples:
    build: .
    environment:
      - LANEFUL_BASE_URL=${LANEFUL_BASE_URL}
      - LANEFUL_AUTH_TOKEN=${LANEFUL_AUTH_TOKEN}
      # ... other environment variables
    command: ["BasicEmailExample"]
```

### Webhook Handler Service (`webhook-handler`)

Runs the webhook handler example with port exposure.

```yaml
services:
  webhook-handler:
    build: .
    ports:
      - "8080:8080"
    command: ["WebhookHandlerExample"]
```

## 🔍 Development Mode

### Live Code Editing

Mount your source code for live editing:

```bash
docker run --rm -it \
  --env-file .env \
  -v "$(pwd)/src:/app/src" \
  laneful-java-examples:latest \
  /bin/bash
```

### Maven Cache

Use Docker volumes for faster builds:

```bash
docker run --rm \
  --env-file .env \
  -v maven-cache:/root/.m2 \
  laneful-java-examples:latest \
  BasicEmailExample
```

## 🧪 Testing

### Run All Examples

```bash
# Run comprehensive example (includes all features)
./run-docker.sh ComprehensiveExample
```

### Test Webhook Handler

```bash
# Start webhook handler service
docker-compose up webhook-handler

# In another terminal, test webhook
curl -X POST http://localhost:8080/webhook \
  -H "Content-Type: application/json" \
  -H "x-webhook-signature: sha256=..." \
  -d '{"event":"delivery","email":"test@example.com",...}'
```

## 🐛 Troubleshooting

### Common Issues

1. **Environment file not found**
   ```bash
   cp env.example .env
   # Edit .env with your credentials
   ```

2. **Docker image not found**
   ```bash
   ./run-docker.sh build
   ```

3. **Permission denied**
   ```bash
   chmod +x run-docker.sh
   ```

4. **Port already in use**
   ```bash
   # Change port in docker-compose.yml
   ports:
     - "8081:8080"  # Use different port
   ```

### Debug Mode

Run with verbose output:

```bash
docker run --rm \
  --env-file .env \
  -e DEBUG=true \
  laneful-java-examples:latest \
  BasicEmailExample
```

### Check Logs

```bash
# Docker Compose logs
docker-compose logs -f

# Container logs
docker logs laneful-java-examples
```

## 🔒 Security

### Credentials Protection

- ✅ `.env` file is gitignored
- ✅ Environment variables are used instead of hardcoded values
- ✅ Docker secrets can be used in production
- ✅ No credentials in Docker images

### Production Deployment

For production, use Docker secrets:

```yaml
services:
  laneful-java-examples:
    secrets:
      - laneful_auth_token
      - laneful_webhook_secret
    environment:
      - LANEFUL_AUTH_TOKEN_FILE=/run/secrets/laneful_auth_token
      - LANEFUL_WEBHOOK_SECRET_FILE=/run/secrets/laneful_webhook_secret

secrets:
  laneful_auth_token:
    file: ./secrets/auth_token.txt
  laneful_webhook_secret:
    file: ./secrets/webhook_secret.txt
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Laneful Java SDK Documentation](../README.md)
- [Laneful API Documentation](https://app.laneful.com/docs)

## 🆘 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Verify your credentials in the `.env` file
3. Ensure Docker is running
4. Check the logs for error messages
5. Open an issue on GitHub

---

**Happy coding! 🚀**
