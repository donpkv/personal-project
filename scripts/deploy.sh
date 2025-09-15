#!/bin/bash

# Career OS - Deployment Script
# This script deploys the Career OS platform to different environments

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default values
ENVIRONMENT="development"
BUILD_IMAGES=false
SKIP_TESTS=false
DOCKER_REGISTRY=""
IMAGE_TAG="latest"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -b|--build-images)
            BUILD_IMAGES=true
            shift
            ;;
        -s|--skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        -r|--registry)
            DOCKER_REGISTRY="$2"
            shift 2
            ;;
        -t|--tag)
            IMAGE_TAG="$2"
            shift 2
            ;;
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  -e, --environment ENV    Target environment (development, staging, production)"
            echo "  -b, --build-images       Build Docker images before deployment"
            echo "  -s, --skip-tests        Skip running tests"
            echo "  -r, --registry REGISTRY Docker registry URL"
            echo "  -t, --tag TAG           Docker image tag (default: latest)"
            echo "  -h, --help              Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0 --environment development"
            echo "  $0 --environment production --build-images --tag v1.0.0"
            exit 0
            ;;
        *)
            print_error "Unknown option $1"
            exit 1
            ;;
    esac
done

print_status "Starting deployment to $ENVIRONMENT environment..."

# Validate environment
validate_environment() {
    case $ENVIRONMENT in
        development|staging|production)
            print_success "Environment '$ENVIRONMENT' is valid"
            ;;
        *)
            print_error "Invalid environment '$ENVIRONMENT'. Valid options: development, staging, production"
            exit 1
            ;;
    esac
}

# Run tests
run_tests() {
    if [ "$SKIP_TESTS" = false ]; then
        print_status "Running tests..."
        
        # Backend tests
        if [ -d "career-os-backend" ]; then
            cd career-os-backend
            print_status "Running backend tests..."
            mvn test -q
            cd ..
        fi
        
        # Frontend tests
        if [ -d "career-os-frontend" ]; then
            cd career-os-frontend
            print_status "Running frontend tests..."
            npm test -- --passWithNoTests --silent
            cd ..
        fi
        
        print_success "All tests passed"
    else
        print_warning "Skipping tests as requested"
    fi
}

# Build Docker images
build_images() {
    if [ "$BUILD_IMAGES" = true ]; then
        print_status "Building Docker images..."
        
        # Set image names
        BACKEND_IMAGE="career-os-backend:$IMAGE_TAG"
        FRONTEND_IMAGE="career-os-frontend:$IMAGE_TAG"
        
        if [ -n "$DOCKER_REGISTRY" ]; then
            BACKEND_IMAGE="$DOCKER_REGISTRY/$BACKEND_IMAGE"
            FRONTEND_IMAGE="$DOCKER_REGISTRY/$FRONTEND_IMAGE"
        fi
        
        # Build backend image
        if [ -d "career-os-backend" ]; then
            print_status "Building backend image: $BACKEND_IMAGE"
            docker build -t "$BACKEND_IMAGE" career-os-backend/
        fi
        
        # Build frontend image
        if [ -d "career-os-frontend" ]; then
            print_status "Building frontend image: $FRONTEND_IMAGE"
            docker build -t "$FRONTEND_IMAGE" career-os-frontend/
        fi
        
        # Push images if registry is specified
        if [ -n "$DOCKER_REGISTRY" ]; then
            print_status "Pushing images to registry..."
            docker push "$BACKEND_IMAGE"
            docker push "$FRONTEND_IMAGE"
        fi
        
        print_success "Docker images built successfully"
    fi
}

# Deploy to development environment
deploy_development() {
    print_status "Deploying to development environment..."
    
    # Start local services with Docker Compose
    if [ -f "docker-compose.yml" ]; then
        docker-compose up -d postgres redis
        print_success "Database services started"
    fi
    
    # Start backend
    if [ -d "career-os-backend" ]; then
        cd career-os-backend
        print_status "Starting backend server..."
        mvn spring-boot:run -Dspring-boot.run.profiles=dev &
        BACKEND_PID=$!
        cd ..
    fi
    
    # Start frontend
    if [ -d "career-os-frontend" ]; then
        cd career-os-frontend
        print_status "Starting frontend server..."
        npm run dev &
        FRONTEND_PID=$!
        cd ..
    fi
    
    print_success "Development environment deployed"
    print_status "Frontend: http://localhost:3000"
    print_status "Backend API: http://localhost:8080/api/v1"
    print_status "API Documentation: http://localhost:8080/swagger-ui.html"
    
    # Save PIDs for cleanup
    echo $BACKEND_PID > .backend.pid
    echo $FRONTEND_PID > .frontend.pid
}

# Deploy to Kubernetes (staging/production)
deploy_kubernetes() {
    print_status "Deploying to Kubernetes ($ENVIRONMENT)..."
    
    # Check if kubectl is available
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl is not installed or not in PATH"
        exit 1
    fi
    
    # Set namespace based on environment
    case $ENVIRONMENT in
        staging)
            NAMESPACE="career-os-staging"
            ;;
        production)
            NAMESPACE="career-os"
            ;;
    esac
    
    # Apply Kubernetes manifests
    if [ -d "career-os-backend/k8s" ]; then
        print_status "Applying Kubernetes manifests..."
        
        # Create namespace
        kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
        
        # Apply manifests
        kubectl apply -f career-os-backend/k8s/namespace.yaml -n $NAMESPACE
        kubectl apply -f career-os-backend/k8s/configmap.yaml -n $NAMESPACE
        
        # Apply secrets only in production
        if [ "$ENVIRONMENT" = "production" ] && [ -f "career-os-backend/k8s/secret.yaml" ]; then
            kubectl apply -f career-os-backend/k8s/secret.yaml -n $NAMESPACE
        fi
        
        kubectl apply -f career-os-backend/k8s/deployment.yaml -n $NAMESPACE
        kubectl apply -f career-os-backend/k8s/service.yaml -n $NAMESPACE
        kubectl apply -f career-os-backend/k8s/ingress.yaml -n $NAMESPACE
        
        # Apply HPA only in production
        if [ "$ENVIRONMENT" = "production" ] && [ -f "career-os-backend/k8s/hpa.yaml" ]; then
            kubectl apply -f career-os-backend/k8s/hpa.yaml -n $NAMESPACE
        fi
        
        print_success "Kubernetes manifests applied"
        
        # Wait for deployment to be ready
        print_status "Waiting for deployment to be ready..."
        kubectl wait --for=condition=available --timeout=300s deployment/career-os-backend -n $NAMESPACE
        kubectl wait --for=condition=available --timeout=300s deployment/career-os-frontend -n $NAMESPACE
        
        # Get service information
        print_status "Deployment information:"
        kubectl get pods -n $NAMESPACE
        kubectl get services -n $NAMESPACE
        kubectl get ingress -n $NAMESPACE
        
        print_success "Deployment to $ENVIRONMENT completed"
    else
        print_error "Kubernetes manifests not found"
        exit 1
    fi
}

# Health check
health_check() {
    print_status "Performing health checks..."
    
    case $ENVIRONMENT in
        development)
            # Check local services
            if curl -f http://localhost:8080/actuator/health &> /dev/null; then
                print_success "Backend health check passed"
            else
                print_error "Backend health check failed"
            fi
            
            if curl -f http://localhost:3000 &> /dev/null; then
                print_success "Frontend health check passed"
            else
                print_error "Frontend health check failed"
            fi
            ;;
        staging|production)
            # Check Kubernetes services
            INGRESS_URL=$(kubectl get ingress -n $NAMESPACE -o jsonpath='{.items[0].spec.rules[0].host}' 2>/dev/null || echo "")
            
            if [ -n "$INGRESS_URL" ]; then
                if curl -f "https://$INGRESS_URL/api/v1/health" &> /dev/null; then
                    print_success "Application health check passed"
                else
                    print_warning "Application health check failed - service might still be starting"
                fi
            else
                print_warning "Could not determine ingress URL for health check"
            fi
            ;;
    esac
}

# Cleanup function
cleanup() {
    if [ "$ENVIRONMENT" = "development" ]; then
        print_status "Cleaning up development processes..."
        
        if [ -f ".backend.pid" ]; then
            BACKEND_PID=$(cat .backend.pid)
            kill $BACKEND_PID 2>/dev/null || true
            rm .backend.pid
        fi
        
        if [ -f ".frontend.pid" ]; then
            FRONTEND_PID=$(cat .frontend.pid)
            kill $FRONTEND_PID 2>/dev/null || true
            rm .frontend.pid
        fi
        
        print_success "Development processes stopped"
    fi
}

# Rollback function
rollback() {
    print_status "Rolling back deployment..."
    
    case $ENVIRONMENT in
        staging|production)
            if command -v kubectl &> /dev/null; then
                kubectl rollout undo deployment/career-os-backend -n $NAMESPACE
                kubectl rollout undo deployment/career-os-frontend -n $NAMESPACE
                print_success "Rollback completed"
            fi
            ;;
        development)
            print_warning "Rollback not supported for development environment"
            ;;
    esac
}

# Main deployment function
main() {
    validate_environment
    
    # Set trap for cleanup on script exit
    trap cleanup EXIT
    
    run_tests
    build_images
    
    case $ENVIRONMENT in
        development)
            deploy_development
            ;;
        staging|production)
            deploy_kubernetes
            ;;
    esac
    
    # Wait a bit for services to start
    sleep 5
    health_check
    
    print_success "Deployment to $ENVIRONMENT completed successfully!"
}

# Handle script interruption
trap 'print_error "Deployment interrupted"; cleanup; exit 1' INT TERM

# Run main function
main
