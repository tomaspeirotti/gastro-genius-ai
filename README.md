# 🍽️ GastroGenius AI

**Tu Asistente de Cocina Inteligente** - Your Intelligent Cooking Assistant

GastroGenius AI is a comprehensive recipe management application powered by artificial intelligence. It not only stores and manages your recipes but uses AI to help you cook smarter, reduce food waste, and discover new flavors.

## ✨ Features

### 🔐 Core Features

- **User Authentication & Authorization** - Secure JWT-based authentication with role-based access control
- **Recipe Management** - Complete CRUD operations for recipes with ingredients, instructions, and metadata
- **Advanced Search & Filtering** - Search recipes by ingredients, categories, difficulty, cooking time, and more
- **Public/Private Recipes** - Share your favorite recipes publicly or keep them private

### 🤖 AI-Powered Features

- **🍳 AI Recipe Generator** - Generate complete recipes from available ingredients using Google Gemini AI
- **🍷 Intelligent Sommelier** - Get expert wine pairing suggestions for any recipe
- **🥗 Nutritional Analyst** - Comprehensive nutritional analysis with macronutrients, vitamins, and health scores

### 🏗️ Technical Features

- **RESTful API** - Well-documented API with Swagger/OpenAPI integration
- **Docker Support** - Complete containerization with Docker and Docker Compose
- **Production Ready** - Health checks, logging, monitoring, and production configurations
- **Clean Architecture** - Domain-driven design with clear separation of concerns

## 🛠️ Technology Stack

### Backend

- **Java 24** - Latest Java features with preview support
- **Spring Boot 3.4.0-SNAPSHOT** - Latest Spring Boot with modern features
- **Spring Security 6** - JWT-based authentication and authorization
- **Spring Data JPA** - Data persistence with Hibernate
- **Spring AI** - Integration with Google Vertex AI Gemini
- **PostgreSQL** - Production-grade database
- **Maven** - Dependency management and build tool

### Infrastructure

- **Docker & Docker Compose** - Containerization and orchestration
- **Nginx** - Reverse proxy and load balancer
- **Redis** - Caching and session storage
- **pgAdmin** - Database administration (development)

### Documentation & Testing

- **Swagger/OpenAPI** - API documentation
- **JUnit 5** - Unit and integration testing
- **Testcontainers** - Integration testing with real database

## 🚀 Quick Start

### Prerequisites

- **Java 24** (with preview features enabled)
- **Docker & Docker Compose**
- **Google Cloud Account** (for Gemini AI API)
- **Maven 3.9+** (if running without Docker)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/gastro-genius-ai.git
cd gastro-genius-ai
```

### 2. Configure Environment Variables

Copy the example environment file and configure your settings:

```bash
cp env.example .env
```

Edit `.env` with your configuration:

```env
# Database Configuration
POSTGRES_DB=gastro_genius_db
POSTGRES_USER=gastro_genius
POSTGRES_PASSWORD=your_secure_password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-signing-key-change-this-in-production
JWT_EXPIRATION=86400000

# Google AI Configuration (Required for AI features)
GOOGLE_AI_API_KEY=your-google-gemini-api-key-here
GOOGLE_AI_PROJECT_ID=your-google-cloud-project-id
GOOGLE_AI_LOCATION=us-central1
GOOGLE_AI_MODEL=gemini-1.5-pro
```

### 3. Deploy with Docker (Recommended)

#### Development Environment

```bash
# Start all services including pgAdmin
./scripts/deploy.sh -e dev

# Or manually
docker-compose --profile dev up -d
```

#### Production Environment

```bash
# Deploy to production
./scripts/deploy.sh -e prod

# Or manually
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --profile prod up -d
```

### 4. Access the Application

Once deployed, you can access:

- **API Endpoints**: http://localhost:8090/api
- **Swagger UI**: http://localhost:8090/api/swagger-ui.html
- **Health Check**: http://localhost:8090/api/actuator/health
- **pgAdmin** (dev only): http://localhost:8080

## 📚 API Documentation

### Authentication Endpoints

```bash
# Register a new user
POST /api/auth/register
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword",
  "firstName": "John",
  "lastName": "Doe"
}

# Login
POST /api/auth/login
{
  "usernameOrEmail": "johndoe",
  "password": "securepassword"
}
```

### Recipe Management

```bash
# Create a recipe
POST /api/recipes
Authorization: Bearer <jwt-token>

# Get user's recipes
GET /api/recipes/my

# Search public recipes
GET /api/recipes/search/public?q=pasta&category=MAIN_COURSE

# Get recipe by ID
GET /api/recipes/{id}
```

### AI Features

```bash
# Generate recipe from ingredients
POST /api/ai/generate-recipe
Authorization: Bearer <jwt-token>
{
  "ingredients": ["chicken", "tomatoes", "basil"],
  "cuisine": "Italian",
  "difficulty": "Medium",
  "saveRecipe": true
}

# Get nutritional analysis
GET /api/ai/recipes/{id}/nutrition
Authorization: Bearer <jwt-token>

# Get wine pairing suggestions
GET /api/ai/recipes/{id}/pairing-suggestion
Authorization: Bearer <jwt-token>
```

For complete API documentation, visit the Swagger UI at `/api/swagger-ui.html` when the application is running.

## 🔧 Development Setup

### Local Development (without Docker)

1. **Start PostgreSQL** (via Docker):

   ```bash
   docker-compose up postgres -d
   ```

2. **Configure Application Properties**:
   Copy `src/main/resources/application-dev.yml` and adjust database settings.

3. **Run the Application**:

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Run Tests**:
   ```bash
   mvn test
   ```

### Project Structure

```
gastro-genius-ai/
├── src/main/java/com/gastrogeniusai/
│   ├── domain/              # Domain entities and business logic
│   ├── application/         # Application services and use cases
│   ├── infrastructure/      # External integrations and configurations
│   └── presentation/        # REST controllers and DTOs
├── src/main/resources/      # Configuration files
├── scripts/                 # Deployment and utility scripts
├── nginx/                   # Nginx configuration
└── docker-compose*.yml     # Docker orchestration
```

## 🏗️ Architecture

GastroGenius AI follows **Clean Architecture** principles:

- **Domain Layer**: Core business entities and rules
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: External services, databases, AI integrations
- **Presentation Layer**: REST API controllers and DTOs

### Key Design Patterns

- **Repository Pattern**: Data access abstraction
- **Service Layer**: Business logic encapsulation
- **DTO Pattern**: Data transfer between layers
- **Factory Pattern**: Object creation
- **Strategy Pattern**: AI service implementations

## 🔒 Security

- **JWT Authentication**: Stateless authentication with refresh tokens
- **Password Encryption**: BCrypt with configurable strength
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Rate Limiting**: API rate limiting via Nginx
- **Input Validation**: Comprehensive validation with custom error handling
- **SQL Injection Protection**: JPA/Hibernate parameter binding

## 🚀 Deployment

### Docker Deployment Profiles

- **Development**: Includes pgAdmin and development tools

  ```bash
  docker-compose --profile dev up -d
  ```

- **Production**: Optimized for production with Nginx
  ```bash
  docker-compose -f docker-compose.yml -f docker-compose.prod.yml --profile prod up -d
  ```

### Environment Variables

| Variable                 | Description             | Default            | Required |
| ------------------------ | ----------------------- | ------------------ | -------- |
| `POSTGRES_DB`            | Database name           | `gastro_genius_db` | ✅       |
| `POSTGRES_USER`          | Database user           | `gastro_genius`    | ✅       |
| `POSTGRES_PASSWORD`      | Database password       | -                  | ✅       |
| `JWT_SECRET`             | JWT signing secret      | -                  | ✅       |
| `JWT_EXPIRATION`         | Token expiration (ms)   | `86400000`         | ❌       |
| `GOOGLE_AI_API_KEY`      | Google Gemini API key   | -                  | ✅       |
| `GOOGLE_AI_PROJECT_ID`   | Google Cloud project ID | -                  | ✅       |
| `GOOGLE_AI_LOCATION`     | AI service location     | `us-central1`      | ❌       |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile   | `dev`              | ❌       |

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest="*IT"
```

### Test Categories

- **Unit Tests**: Service and utility class testing
- **Integration Tests**: Database and API endpoint testing
- **AI Service Tests**: Mocked AI service interactions

## 📊 Monitoring & Observability

### Health Checks

- Application health: `/api/actuator/health`
- Database connectivity
- AI service availability

### Logging

- Structured JSON logging in production
- Configurable log levels
- Request/response logging for debugging

### Metrics

- Spring Boot Actuator metrics
- Custom business metrics
- Database performance metrics

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Follow the coding standards**:
   - Use descriptive variable and method names
   - Follow single responsibility principle
   - Add comprehensive JavaDoc comments
   - Write tests for new features
4. **Commit your changes**: `git commit -m 'Add amazing feature'`
5. **Push to the branch**: `git push origin feature/amazing-feature`
6. **Open a Pull Request**

### Code Style Guidelines

- **Java Google Format** for formatting
- **ESLint** for any JavaScript code
- **Clean Code** principles
- **SOLID** design principles
- **Domain-Driven Design** patterns

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Team** for the excellent Spring Boot and Spring AI frameworks
- **Google** for the powerful Gemini AI API
- **PostgreSQL** community for the robust database
- **Docker** for simplifying deployment and development

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/your-username/gastro-genius-ai/issues) page
2. Review the API documentation at `/api/swagger-ui.html`
3. Check application logs: `docker logs gastro-genius-app`
4. Create a new issue with detailed information

---

**Made with ❤️ and ☕ by the GastroGenius AI Team**

_Happy cooking! 🍳_
