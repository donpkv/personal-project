#!/bin/bash

echo "🚀 Setting up Career OS Backend on macOS..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    print_warning "Homebrew not found. Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
fi

# Install required software
print_status "Installing required software..."
brew install openjdk@21 maven postgresql@15 redis git

# Start services
print_status "Starting PostgreSQL and Redis services..."
brew services start postgresql@15
brew services start redis

# Wait for PostgreSQL to start
sleep 3

# Set Java environment
print_status "Configuring Java 21..."
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH

# Setup PostgreSQL database
print_status "Setting up PostgreSQL database..."

# Check if PostgreSQL is running
if ! pg_isready -q; then
    print_error "PostgreSQL is not running. Please start it manually:"
    echo "brew services start postgresql@15"
    exit 1
fi

# Create database and user
print_status "Creating database and user..."
psql postgres -c "DROP DATABASE IF EXISTS career_os;"
psql postgres -c "DROP USER IF EXISTS career_os_user;"
psql postgres -f setup-database.sql

if [ $? -eq 0 ]; then
    print_status "Database setup completed successfully!"
else
    print_error "Database setup failed. Please check PostgreSQL installation."
    exit 1
fi

# Verify Java installation
print_status "Verifying installations..."
java -version
mvn -version
psql --version
redis-cli --version

# Test database connection
print_status "Testing database connection..."
PGPASSWORD=career_os_pass psql -h localhost -U career_os_user -d career_os -c "SELECT version();"

if [ $? -eq 0 ]; then
    print_status "Database connection successful!"
else
    print_error "Database connection failed!"
    exit 1
fi

# Create environment file
print_status "Creating environment configuration..."
cat > .env << EOF
# Java Configuration
JAVA_HOME=$JAVA_HOME
JAVA_VERSION=21

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=career_os
DB_USERNAME=career_os_user
DB_PASSWORD=career_os_pass

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-in-production-$(date +%s)
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
EOF

print_status "Environment file created: .env"

# Build and test the application
print_status "Building the application..."
mvn clean compile

if [ $? -eq 0 ]; then
    print_status "Build successful!"
    
    echo ""
    echo "🎉 Setup completed successfully!"
    echo ""
    echo "To start the application:"
    echo "  mvn spring-boot:run"
    echo ""
    echo "Or run in background:"
    echo "  nohup mvn spring-boot:run > app.log 2>&1 &"
    echo ""
    echo "Application will be available at: http://localhost:8080/api/v1"
    echo ""
    echo "Database connection details:"
    echo "  Host: localhost"
    echo "  Port: 5432"
    echo "  Database: career_os"
    echo "  Username: career_os_user"
    echo "  Password: career_os_pass"
    
else
    print_error "Build failed. Please check the logs above."
    exit 1
fi
