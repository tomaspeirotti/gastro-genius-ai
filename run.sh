#!/bin/bash

# GastroGenius AI - Development Runner
# This script loads environment variables from .env file and runs the Spring Boot application

echo "🍳 Starting GastroGenius AI..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found!"
    echo "Please copy env.example to .env and configure your settings:"
    echo "  cp env.example .env"
    exit 1
fi

echo "📄 Loading environment variables from .env file..."

# Load environment variables from .env file
# This handles comments and empty lines properly
set -a  # automatically export all variables
source .env
set +a  # stop auto-exporting

echo "🚀 Starting Spring Boot application..."
echo "📍 Server will be available at: http://localhost:${SERVER_PORT:-8090}/api"
echo "📚 API Documentation: http://localhost:${SERVER_PORT:-8090}/api/swagger-ui.html"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

# Run the Spring Boot application
mvn spring-boot:run
