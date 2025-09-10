#!/bin/bash

# Laneful Java SDK Examples - Docker Runner
# This script helps you run the Java examples in Docker with real credentials

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Laneful Java SDK Examples - Docker Runner${NC}"
echo "=================================================="

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating from template...${NC}"
    if [ -f "env.example" ]; then
        cp env.example .env
        echo -e "${GREEN}✅ Created .env file from template${NC}"
        echo -e "${YELLOW}📝 Please review and update the .env file with your actual credentials${NC}"
    else
        echo -e "${RED}❌ env.example file not found. Please create .env file manually.${NC}"
        exit 1
    fi
fi

# Function to show usage
show_usage() {
    echo -e "${BLUE}Usage:${NC}"
    echo "  $0 [example-name]                    # Run specific example"
    echo "  $0 build                            # Build Docker image"
    echo "  $0 shell                            # Open shell in container"
    echo "  $0 compose [service]                # Run with docker-compose"
    echo "  $0 clean                            # Clean up Docker resources"
    echo ""
    echo -e "${BLUE}Available examples:${NC}"
    echo "  BasicEmailExample"
    echo "  HTMLEmailWithTrackingExample"
    echo "  TemplateEmailExample"
    echo "  AttachmentEmailExample"
    echo "  MultipleRecipientsExample"
    echo "  ScheduledEmailExample"
    echo "  BatchEmailExample"
    echo "  ErrorHandlingExample"
    echo "  WebhookHandlerExample"
    echo "  ComprehensiveExample"
    echo ""
    echo -e "${BLUE}Examples:${NC}"
    echo "  $0 BasicEmailExample                # Run basic email example"
    echo "  $0 build                           # Build the Docker image"
    echo "  $0 compose                         # Run with docker-compose"
    echo "  $0 compose webhook-handler         # Run webhook handler service"
}

# Function to build Docker image
build_image() {
    echo -e "${BLUE}🔨 Building Docker image...${NC}"
    docker build -t laneful-java-examples:latest .
    echo -e "${GREEN}✅ Docker image built successfully${NC}"
}

# Function to run specific example
run_example() {
    local example_name=$1
    echo -e "${BLUE}🎯 Running example: $example_name${NC}"
    
    # Check if image exists
    if ! docker image inspect laneful-java-examples:latest >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Docker image not found. Building...${NC}"
        build_image
    fi
    
    # Run the example
    docker run --rm \
        --env-file .env \
        -v "$(pwd)/src:/app/src" \
        laneful-java-examples:latest \
        "$example_name"
}

# Function to open shell in container
open_shell() {
    echo -e "${BLUE}🐚 Opening shell in container...${NC}"
    
    # Check if image exists
    if ! docker image inspect laneful-java-examples:latest >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Docker image not found. Building...${NC}"
        build_image
    fi
    
    docker run --rm -it \
        --env-file .env \
        -v "$(pwd)/src:/app/src" \
        laneful-java-examples:latest \
        /bin/bash
}

# Function to run with docker-compose
run_compose() {
    local service=$1
    echo -e "${BLUE}🐳 Running with docker-compose...${NC}"
    
    if [ -n "$service" ]; then
        echo -e "${BLUE}🎯 Running service: $service${NC}"
        docker-compose up --build "$service"
    else
        echo -e "${BLUE}🎯 Running default service${NC}"
        docker-compose up --build
    fi
}

# Function to clean up Docker resources
clean_up() {
    echo -e "${BLUE}🧹 Cleaning up Docker resources...${NC}"
    
    # Stop and remove containers
    docker-compose down 2>/dev/null || true
    
    # Remove images
    docker rmi laneful-java-examples:latest 2>/dev/null || true
    
    # Remove volumes
    docker volume prune -f
    
    echo -e "${GREEN}✅ Cleanup completed${NC}"
}

# Main script logic
case "${1:-}" in
    "build")
        build_image
        ;;
    "shell")
        open_shell
        ;;
    "compose")
        run_compose "$2"
        ;;
    "clean")
        clean_up
        ;;
    "help"|"-h"|"--help")
        show_usage
        ;;
    "")
        echo -e "${YELLOW}No example specified. Showing usage:${NC}"
        show_usage
        ;;
    *)
        run_example "$1"
        ;;
esac

