#!/bin/bash

# Career OS - Development Environment Setup Script
# This script sets up the complete development environment for Career OS

set -e  # Exit on any error

echo "🚀 Setting up Career OS Development Environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Check if required tools are installed
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_success "Java $JAVA_VERSION found"
        else
            print_error "Java 17 or higher required. Found Java $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java not found. Please install Java 17 or higher"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        print_success "Maven found"
    else
        print_error "Maven not found. Please install Maven"
        exit 1
    fi
    
    # Check Node.js
    if command -v node &> /dev/null; then
        NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
        if [ "$NODE_VERSION" -ge 18 ]; then
            print_success "Node.js $NODE_VERSION found"
        else
            print_error "Node.js 18 or higher required. Found Node.js $NODE_VERSION"
            exit 1
        fi
    else
        print_error "Node.js not found. Please install Node.js 18 or higher"
        exit 1
    fi
    
    # Check npm
    if command -v npm &> /dev/null; then
        print_success "npm found"
    else
        print_error "npm not found. Please install npm"
        exit 1
    fi
    
    # Check Flutter
    if command -v flutter &> /dev/null; then
        print_success "Flutter found"
    else
        print_warning "Flutter not found. Mobile development will not be available"
    fi
    
    # Check Docker
    if command -v docker &> /dev/null; then
        print_success "Docker found"
    else
        print_warning "Docker not found. Containerization features will not be available"
    fi
    
    # Check Docker Compose
    if command -v docker-compose &> /dev/null; then
        print_success "Docker Compose found"
    else
        print_warning "Docker Compose not found. Local services orchestration will not be available"
    fi
}

# Setup backend
setup_backend() {
    print_status "Setting up backend..."
    
    if [ -d "career-os-backend" ]; then
        cd career-os-backend
        
        # Clean and install dependencies
        print_status "Installing backend dependencies..."
        mvn clean install -DskipTests
        
        print_success "Backend setup completed"
        cd ..
    else
        print_error "Backend directory not found"
        exit 1
    fi
}

# Setup frontend
setup_frontend() {
    print_status "Setting up frontend..."
    
    if [ -d "career-os-frontend" ]; then
        cd career-os-frontend
        
        # Install dependencies
        print_status "Installing frontend dependencies..."
        npm install
        
        # Build the project
        print_status "Building frontend..."
        npm run build
        
        print_success "Frontend setup completed"
        cd ..
    else
        print_error "Frontend directory not found"
        exit 1
    fi
}

# Setup mobile app
setup_mobile() {
    print_status "Setting up mobile app..."
    
    if [ -d "career-os-mobile" ] && command -v flutter &> /dev/null; then
        cd career-os-mobile
        
        # Get Flutter dependencies
        print_status "Getting Flutter dependencies..."
        flutter pub get
        
        # Run Flutter doctor
        print_status "Running Flutter doctor..."
        flutter doctor
        
        print_success "Mobile app setup completed"
        cd ..
    elif [ -d "career-os-mobile" ]; then
        print_warning "Mobile app directory found but Flutter not installed"
    else
        print_warning "Mobile app directory not found"
    fi
}

# Setup database (Docker)
setup_database() {
    if command -v docker &> /dev/null && command -v docker-compose &> /dev/null; then
        print_status "Setting up database services..."
        
        # Create docker-compose.yml if it doesn't exist
        if [ ! -f "docker-compose.yml" ]; then
            print_status "Creating docker-compose.yml..."
            cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: career-os-postgres
    environment:
      POSTGRES_DB: careeros
      POSTGRES_USER: careeros
      POSTGRES_PASSWORD: careeros123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - career-os-network

  redis:
    image: redis:7-alpine
    container_name: career-os-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - career-os-network

  adminer:
    image: adminer:4-standalone
    container_name: career-os-adminer
    ports:
      - "8080:8080"
    networks:
      - career-os-network

volumes:
  postgres_data:
  redis_data:

networks:
  career-os-network:
    driver: bridge
EOF
        fi
        
        # Start database services
        print_status "Starting database services..."
        docker-compose up -d postgres redis
        
        # Wait for services to be ready
        print_status "Waiting for database services to be ready..."
        sleep 10
        
        print_success "Database services are running"
        print_status "PostgreSQL: localhost:5432 (user: careeros, password: careeros123, db: careeros)"
        print_status "Redis: localhost:6379"
        print_status "Adminer (DB Admin): http://localhost:8080"
    else
        print_warning "Docker not available. Please set up PostgreSQL and Redis manually"
    fi
}

# Create environment files
create_env_files() {
    print_status "Creating environment configuration files..."
    
    # Backend application-dev.yml
    if [ -d "career-os-backend/src/main/resources" ]; then
        cat > career-os-backend/src/main/resources/application-dev.yml << 'EOF'
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/careeros
    username: careeros
    password: careeros123
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
  
  profiles:
    active: dev

app:
  jwt:
    secret: career-os-dev-secret-key-change-in-production
    expiration: 86400000
  
  openai:
    api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
  
  cors:
    allowed-origins: http://localhost:3000,http://localhost:3001

logging:
  level:
    com.careeros: DEBUG
    org.springframework.security: DEBUG
EOF
        print_success "Backend development configuration created"
    fi
    
    # Frontend .env.local
    if [ -d "career-os-frontend" ]; then
        cat > career-os-frontend/.env.local << 'EOF'
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws

# Authentication
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=career-os-dev-secret

# Features
NEXT_PUBLIC_ENABLE_AI_FEATURES=true
NEXT_PUBLIC_ENABLE_SOCIAL_FEATURES=true
NEXT_PUBLIC_ENABLE_MENTORSHIP=true
NEXT_PUBLIC_ENABLE_CERTIFICATIONS=true

# Analytics
NEXT_PUBLIC_ENABLE_ANALYTICS=false
EOF
        print_success "Frontend development configuration created"
    fi
    
    # Mobile app config
    if [ -d "career-os-mobile" ]; then
        mkdir -p career-os-mobile/lib/config
        cat > career-os-mobile/lib/config/app_config_dev.dart << 'EOF'
class AppConfigDev {
  static const String apiBaseUrl = 'http://localhost:8080/api/v1';
  static const String wsBaseUrl = 'ws://localhost:8080/ws';
  
  static const bool enableAIFeatures = true;
  static const bool enableSocialFeatures = true;
  static const bool enableMentorship = true;
  static const bool enableCertifications = true;
  static const bool enableAnalytics = false;
  
  static const String environment = 'development';
}
EOF
        print_success "Mobile development configuration created"
    fi
}

# Setup IDE configurations
setup_ide_config() {
    print_status "Setting up IDE configurations..."
    
    # VS Code settings
    mkdir -p .vscode
    cat > .vscode/settings.json << 'EOF'
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  "typescript.preferences.importModuleSpecifier": "relative",
  "eslint.workingDirectories": ["career-os-frontend"],
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true,
    "source.fixAll.eslint": true
  },
  "files.exclude": {
    "**/node_modules": true,
    "**/target": true,
    "**/build": true,
    "**/.dart_tool": true
  },
  "search.exclude": {
    "**/node_modules": true,
    "**/target": true,
    "**/build": true,
    "**/.dart_tool": true
  }
}
EOF

    cat > .vscode/launch.json << 'EOF'
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Launch Career OS Backend",
      "request": "launch",
      "mainClass": "com.careeros.CareerOSApplication",
      "projectName": "career-os-backend",
      "args": "--spring.profiles.active=dev"
    },
    {
      "name": "Launch Career OS Frontend",
      "type": "node",
      "request": "launch",
      "cwd": "${workspaceFolder}/career-os-frontend",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "dev"]
    }
  ]
}
EOF

    cat > .vscode/extensions.json << 'EOF'
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "ms-vscode.vscode-typescript-next",
    "bradlc.vscode-tailwindcss",
    "dart-code.flutter",
    "ms-kubernetes-tools.vscode-kubernetes-tools",
    "ms-vscode.vscode-docker",
    "esbenp.prettier-vscode",
    "dbaeumer.vscode-eslint"
  ]
}
EOF

    print_success "VS Code configuration created"
}

# Run tests
run_tests() {
    print_status "Running tests..."
    
    # Backend tests
    if [ -d "career-os-backend" ]; then
        cd career-os-backend
        print_status "Running backend tests..."
        mvn test
        cd ..
    fi
    
    # Frontend tests
    if [ -d "career-os-frontend" ]; then
        cd career-os-frontend
        print_status "Running frontend tests..."
        npm test -- --passWithNoTests
        cd ..
    fi
    
    # Mobile tests
    if [ -d "career-os-mobile" ] && command -v flutter &> /dev/null; then
        cd career-os-mobile
        print_status "Running mobile tests..."
        flutter test
        cd ..
    fi
    
    print_success "All tests completed"
}

# Main setup function
main() {
    echo "🎯 Career OS Development Environment Setup"
    echo "=========================================="
    
    check_prerequisites
    setup_backend
    setup_frontend
    setup_mobile
    setup_database
    create_env_files
    setup_ide_config
    
    if [ "$1" = "--with-tests" ]; then
        run_tests
    fi
    
    echo ""
    echo "🎉 Setup completed successfully!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Start the database services: docker-compose up -d"
    echo "2. Start the backend: cd career-os-backend && mvn spring-boot:run"
    echo "3. Start the frontend: cd career-os-frontend && npm run dev"
    echo "4. (Optional) Start mobile development: cd career-os-mobile && flutter run"
    echo ""
    echo "🔗 Access URLs:"
    echo "- Frontend: http://localhost:3000"
    echo "- Backend API: http://localhost:8080/api/v1"
    echo "- Database Admin: http://localhost:8080 (Adminer)"
    echo "- API Documentation: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "📚 For more information, check the documentation files:"
    echo "- README.md"
    echo "- TECHNICAL_DOCUMENTATION.md"
    echo "- API_DOCUMENTATION.md"
    echo "- DEPLOYMENT_GUIDE.md"
}

# Run main function with all arguments
main "$@"
