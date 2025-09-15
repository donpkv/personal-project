# Career OS - Enterprise Skill Development Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/career-os/career-os)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-orange)](package.json)

## 🚀 Overview

Career OS is an enterprise-grade skill development and job readiness platform that helps professionals enhance their skills, track learning progress, and build ATS-compliant resumes. Built with modern technologies and designed for scalability, it features AI-powered recommendations and comprehensive progress tracking.

### 🎯 Key Features

- **📚 Resource Sharing Hub** - Curated external courses, tutorials, and learning materials
- **📊 Progress Tracking** - Comprehensive learning analytics and milestone tracking
- **🤖 AI-Powered Recommendations** - Personalized skill development paths using OpenAI
- **📄 ATS Resume Builder** - Industry-standard resume creation with optimization scoring
- **🔍 Resume Analyzer** - AI-driven ATS scoring and improvement suggestions
- **📱 Progressive Web App** - Mobile-first responsive design with offline capabilities
- **🔐 Enterprise Security** - JWT authentication, role-based access, and data encryption

## 🏗️ Architecture

### Technology Stack

**Backend (Java Ecosystem):**
- Spring Boot 3.2 with Spring Security
- PostgreSQL with JPA/Hibernate
- Redis for caching and sessions
- Apache Kafka for event streaming
- Docker & Kubernetes deployment
- OpenAI API integration

**Frontend (React Ecosystem):**
- Next.js 14 with TypeScript
- Tailwind CSS for styling
- React Query for state management
- Progressive Web App capabilities
- Responsive mobile-first design

**Infrastructure:**
- Kubernetes with auto-scaling (HPA)
- NGINX Ingress with SSL termination
- Prometheus + Grafana monitoring
- ELK Stack for logging

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Load Balancer                            │
│                     (NGINX Ingress)                            │
└─────────────────────┬───────────────────────────────────────────┘
                      │
              ┌───────┴───────┐
              │               │
    ┌─────────▼─────────┐   ┌─▼──────────────┐
    │   Frontend Pods   │   │  Backend Pods  │
    │   (Next.js)       │   │ (Spring Boot)  │
    │   Auto-scaling    │   │ Auto-scaling   │
    └───────────────────┘   └─┬──────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
    ┌─────────▼─────────┐   ┌─▼─────────┐   ┌─▼──────────┐
    │   PostgreSQL      │   │   Redis   │   │   Kafka    │
    │   (Primary DB)    │   │ (Cache)   │   │ (Events)   │
    └───────────────────┘   └───────────┘   └────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 21+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **Kubernetes cluster** (for production)
- **PostgreSQL 13+**
- **Redis 6+**

### Local Development Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/career-os/career-os.git
   cd career-os
   ```

2. **Backend Setup:**
   ```bash
   cd career-os-backend
   
   # Configure database
   cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
   # Edit database credentials
   
   # Run with Maven
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Frontend Setup:**
   ```bash
   cd career-os-frontend
   
   # Install dependencies
   npm install
   
   # Configure environment
   cp .env.local.example .env.local
   # Edit API endpoints
   
   # Run development server
   npm run dev
   ```

4. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api/v1
   - API Documentation: http://localhost:8080/api/v1/swagger-ui.html

### Docker Development

```bash
# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 📦 Production Deployment

### Kubernetes Deployment

1. **Create namespace and secrets:**
   ```bash
   kubectl apply -f career-os-backend/k8s/namespace.yaml
   kubectl apply -f career-os-backend/k8s/secret.yaml
   ```

2. **Deploy database and cache:**
   ```bash
   kubectl apply -f infrastructure/postgresql.yaml
   kubectl apply -f infrastructure/redis.yaml
   kubectl apply -f infrastructure/kafka.yaml
   ```

3. **Deploy backend:**
   ```bash
   kubectl apply -f career-os-backend/k8s/
   ```

4. **Deploy frontend:**
   ```bash
   kubectl apply -f career-os-frontend/k8s/
   ```

5. **Configure ingress:**
   ```bash
   kubectl apply -f infrastructure/ingress.yaml
   ```

### Environment Variables

**Backend (.env):**
```env
SPRING_PROFILES_ACTIVE=prod
DB_HOST=postgres-service
DB_USERNAME=career_os_user
DB_PASSWORD=your-secure-password
REDIS_HOST=redis-service
REDIS_PASSWORD=your-redis-password
JWT_SECRET=your-jwt-secret-key
OPENAI_API_KEY=your-openai-api-key
```

**Frontend (.env.local):**
```env
NEXT_PUBLIC_API_URL=https://api.career-os.com/api/v1
NEXT_PUBLIC_APP_URL=https://career-os.com
NEXTAUTH_SECRET=your-nextauth-secret
```

## 🔧 Configuration

### Database Migration

The application uses Flyway for database migrations. Migrations are automatically applied on startup.

```sql
-- Example migration: V1__Create_users_table.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### AI Integration

Configure OpenAI integration in `application.yml`:

```yaml
ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    model: gpt-4-turbo-preview
    max-tokens: 2000
    temperature: 0.7
```

### Monitoring

Access monitoring dashboards:
- **Prometheus**: http://monitoring.career-os.com/prometheus
- **Grafana**: http://monitoring.career-os.com/grafana
- **Application Metrics**: http://api.career-os.com/actuator/prometheus

## 🧪 Testing

### Backend Testing
```bash
cd career-os-backend
./mvnw test
./mvnw test -Dspring.profiles.active=test
```

### Frontend Testing
```bash
cd career-os-frontend
npm test
npm run test:coverage
npm run test:e2e
```

### Load Testing
```bash
# Using k6
k6 run scripts/load-test.js

# Using Artillery
artillery run scripts/load-test.yml
```

## 📊 API Documentation

### Authentication Endpoints
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh JWT token
- `POST /api/v1/auth/logout` - User logout

### Core Endpoints
- `GET /api/v1/skills` - List available skills
- `GET /api/v1/resources` - Get learning resources
- `POST /api/v1/progress` - Update learning progress
- `GET /api/v1/resumes` - User resumes
- `POST /api/v1/resumes/analyze` - Analyze resume ATS score

### AI Endpoints
- `POST /api/v1/ai/recommendations` - Get skill recommendations
- `POST /api/v1/ai/resume-feedback` - Get resume improvement suggestions

## 🔒 Security Features

- **JWT Authentication** with refresh tokens
- **Role-based Access Control** (RBAC)
- **Rate Limiting** on API endpoints
- **Input Validation** and sanitization
- **SQL Injection Protection** via JPA
- **XSS Protection** with CSP headers
- **HTTPS Enforcement** in production
- **Password Hashing** with BCrypt

## 📈 Scalability Features

- **Horizontal Pod Autoscaling** (3-10 replicas)
- **Database Connection Pooling** (HikariCP)
- **Redis Caching** for frequently accessed data
- **CDN Integration** for static assets
- **Kafka Event Streaming** for async processing
- **Load Balancing** with NGINX Ingress

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow the existing code style and conventions
- Write comprehensive tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting PR
- Use conventional commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙋‍♂️ Support

- **Documentation**: [docs.career-os.com](https://docs.career-os.com)
- **Issues**: [GitHub Issues](https://github.com/career-os/career-os/issues)
- **Discussions**: [GitHub Discussions](https://github.com/career-os/career-os/discussions)
- **Email**: support@career-os.com

## 🎯 Roadmap

### Phase 1 (Current)
- ✅ Core platform architecture
- ✅ User authentication and profiles
- ✅ Resource sharing and progress tracking
- ✅ Basic resume builder

### Phase 2 (Q2 2024)
- 🔄 Advanced AI recommendations
- 🔄 Mobile app development
- 🔄 Integration with major learning platforms
- 🔄 Advanced analytics dashboard

### Phase 3 (Q3 2024)
- 📋 Skill assessments and certifications
- 📋 Mentorship matching system
- 📋 Job board integration
- 📋 Company partnerships

---

**Built with ❤️ by the Career OS Team**
