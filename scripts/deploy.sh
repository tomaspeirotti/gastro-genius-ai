#!/bin/bash

# GastroGenius AI Deployment Script
# This script handles the deployment of the application in different environments

set -e

# Default values
ENVIRONMENT="dev"
BUILD_FRESH="false"
SKIP_TESTS="false"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --environment ENV    Target environment (dev, prod) [default: dev]"
    echo "  -f, --fresh-build        Force fresh build (rebuild Docker images)"
    echo "  -s, --skip-tests         Skip running tests before deployment"
    echo "  -h, --help               Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -e dev -f             Deploy to development with fresh build"
    echo "  $0 -e prod               Deploy to production"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -f|--fresh-build)
            BUILD_FRESH="true"
            shift
            ;;
        -s|--skip-tests)
            SKIP_TESTS="true"
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|prod)$ ]]; then
    print_error "Invalid environment: $ENVIRONMENT. Must be 'dev' or 'prod'"
    exit 1
fi

print_info "Starting deployment for environment: $ENVIRONMENT"

# Check if required files exist
if [[ ! -f "docker-compose.yml" ]]; then
    print_error "docker-compose.yml not found in current directory"
    exit 1
fi

if [[ "$ENVIRONMENT" == "prod" && ! -f "docker-compose.prod.yml" ]]; then
    print_error "docker-compose.prod.yml not found for production deployment"
    exit 1
fi

# Check if .env file exists for production
if [[ "$ENVIRONMENT" == "prod" && ! -f ".env" ]]; then
    print_warning ".env file not found. Make sure environment variables are set."
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Run tests (unless skipped)
if [[ "$SKIP_TESTS" == "false" ]]; then
    print_info "Running tests..."
    if ! mvn test -q; then
        print_error "Tests failed. Deployment aborted."
        exit 1
    fi
    print_info "Tests passed successfully"
fi

# Stop existing containers
print_info "Stopping existing containers..."
if [[ "$ENVIRONMENT" == "prod" ]]; then
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
else
    docker-compose down
fi

# Build fresh images if requested
if [[ "$BUILD_FRESH" == "true" ]]; then
    print_info "Building fresh Docker images..."
    if [[ "$ENVIRONMENT" == "prod" ]]; then
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
    else
        docker-compose build --no-cache
    fi
fi

# Deploy based on environment
print_info "Deploying to $ENVIRONMENT environment..."

if [[ "$ENVIRONMENT" == "prod" ]]; then
    # Production deployment
    print_info "Starting production deployment..."
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml --profile prod up -d
    
    # Wait for services to be healthy
    print_info "Waiting for services to be healthy..."
    sleep 30
    
    # Check application health
    max_attempts=30
    attempt=1
    while [[ $attempt -le $max_attempts ]]; do
        if curl -f http://localhost:8090/api/actuator/health >/dev/null 2>&1; then
            print_info "Application is healthy!"
            break
        fi
        print_warning "Attempt $attempt/$max_attempts: Application not ready yet..."
        sleep 10
        ((attempt++))
    done
    
    if [[ $attempt -gt $max_attempts ]]; then
        print_error "Application failed to start properly"
        exit 1
    fi
    
else
    # Development deployment
    print_info "Starting development deployment..."
    docker-compose --profile dev up -d
fi

# Show deployment status
print_info "Deployment completed successfully!"
print_info "Services status:"
docker-compose ps

# Show useful URLs
echo ""
print_info "Application URLs:"
echo "  - API: http://localhost:8090/api"
echo "  - Swagger UI: http://localhost:8090/api/swagger-ui.html"
echo "  - Health Check: http://localhost:8090/api/actuator/health"

if [[ "$ENVIRONMENT" == "dev" ]]; then
    echo "  - pgAdmin: http://localhost:8080"
fi

if [[ "$ENVIRONMENT" == "prod" ]]; then
    echo "  - Nginx: http://localhost (if configured)"
fi

print_info "Deployment script completed!"
