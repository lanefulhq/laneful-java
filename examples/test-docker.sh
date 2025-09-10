#!/bin/bash

# Test script for Docker setup
# This script tests the Docker setup with real credentials

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 Testing Docker Setup for Laneful Java SDK Examples${NC}"
echo "=============================================================="

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo -e "${RED}❌ .env file not found${NC}"
    echo -e "${YELLOW}Please create .env file with your credentials:${NC}"
    echo "  cp env.example .env"
    echo "  # Edit .env with your real credentials"
    exit 1
fi

echo -e "${GREEN}✅ .env file found${NC}"

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running${NC}"
    echo -e "${YELLOW}Please start Docker and try again${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker is running${NC}"

# Build the Docker image
echo -e "${BLUE}🔨 Building Docker image...${NC}"
if docker build -t laneful-java-examples:latest . >/dev/null 2>&1; then
    echo -e "${GREEN}✅ Docker image built successfully${NC}"
else
    echo -e "${RED}❌ Failed to build Docker image${NC}"
    exit 1
fi

# Test basic email example
echo -e "${BLUE}📧 Testing BasicEmailExample...${NC}"
if docker run --rm --env-file .env laneful-java-examples:latest BasicEmailExample >/dev/null 2>&1; then
    echo -e "${GREEN}✅ BasicEmailExample test passed${NC}"
else
    echo -e "${YELLOW}⚠️  BasicEmailExample test failed (this might be expected if credentials are invalid)${NC}"
fi

# Test error handling example
echo -e "${BLUE}⚠️  Testing ErrorHandlingExample...${NC}"
if docker run --rm --env-file .env laneful-java-examples:latest ErrorHandlingExample >/dev/null 2>&1; then
    echo -e "${GREEN}✅ ErrorHandlingExample test passed${NC}"
else
    echo -e "${YELLOW}⚠️  ErrorHandlingExample test failed${NC}"
fi

# Test webhook handler example
echo -e "${BLUE}🔗 Testing WebhookHandlerExample...${NC}"
if docker run --rm --env-file .env laneful-java-examples:latest WebhookHandlerExample >/dev/null 2>&1; then
    echo -e "${GREEN}✅ WebhookHandlerExample test passed${NC}"
else
    echo -e "${YELLOW}⚠️  WebhookHandlerExample test failed${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Docker setup test completed!${NC}"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "1. Verify your credentials in .env file"
echo "2. Run a real example: ./run-docker.sh BasicEmailExample"
echo "3. Check the logs for any errors"
echo ""
echo -e "${BLUE}Available examples:${NC}"
echo "  ./run-docker.sh BasicEmailExample"
echo "  ./run-docker.sh HTMLEmailWithTrackingExample"
echo "  ./run-docker.sh ComprehensiveExample"
echo ""
echo -e "${BLUE}For help:${NC}"
echo "  ./run-docker.sh help"
