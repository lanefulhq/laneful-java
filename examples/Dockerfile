# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# First, build the SDK jar from the parent directory
WORKDIR /sdk
COPY src ./src
COPY pom.xml ./

# Debug: Check if source files were copied
RUN echo "🔍 DEBUG: Checking SDK source files..." && \
    find ./src -name "*.java" | head -5 && \
    echo "🔍 DEBUG: Checking LanefulClient.java..." && \
    ls -la ./src/main/java/com/laneful/client/LanefulClient.java

RUN mvn clean install -DskipTests -Dgpg.skip

# Debug: List the target directory to see what was built
RUN ls -la /sdk/target/

# Now switch to examples directory and copy the examples source files
WORKDIR /app
COPY examples/ ./

# Install the locally built SDK jar into the local Maven repository
# Use a wildcard to find the jar file
RUN SDK_JAR=$(find /sdk/target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1) && \
    echo "Found SDK jar: $SDK_JAR" && \
    mvn install:install-file \
    -Dfile="$SDK_JAR" \
    -DgroupId=com.laneful \
    -DartifactId=laneful-java \
    -Dversion=1.1.0 \
    -Dpackaging=jar

# Build the examples project
RUN mvn clean compile -DskipTests

# Debug: List the compiled classes
RUN find /app/target -name "*.class" | head -10

# Create a script to run examples with environment variables
RUN echo '#!/bin/bash\n\
echo "🚀 Laneful Java SDK Examples"\n\
echo "================================"\n\
echo "Base URL: $LANEFUL_BASE_URL"\n\
echo "From Email: $LANEFUL_FROM_EMAIL"\n\
echo "To Emails: $LANEFUL_TO_EMAILS"\n\
echo "Template ID: $LANEFUL_TEMPLATE_ID"\n\
echo "Webhook Secret: ${LANEFUL_WEBHOOK_SECRET:0:8}..."\n\
echo "Webhook URL: $LANEFUL_WEBHOOK_URL"\n\
echo ""\n\
\n\
# Run the specified example or show menu\n\
if [ -n "$1" ]; then\n\
    echo "🎯 Running example: $1"\n\
    mvn exec:java -Dexec.mainClass="com.laneful.examples.$1"\n\
else\n\
    echo "📋 Available examples:"\n\
    echo "  BasicEmailExample"\n\
    echo "  HTMLEmailWithTrackingExample"\n\
    echo "  TemplateEmailExample"\n\
    echo "  AttachmentEmailExample"\n\
    echo "  MultipleRecipientsExample"\n\
    echo "  ScheduledEmailExample"\n\
    echo "  BatchEmailExample"\n\
    echo "  ErrorHandlingExample"\n\
    echo "  WebhookHandlerExample"\n\
    echo "  ComprehensiveExample"\n\
    echo ""\n\
    echo "Usage: docker run --env-file .env laneful-java-examples [ExampleName]"\n\
    echo "Example: docker run --env-file .env laneful-java-examples BasicEmailExample"\n\
fi' > /app/run-example.sh && chmod +x /app/run-example.sh

# Set the entrypoint
ENTRYPOINT ["/app/run-example.sh"]
