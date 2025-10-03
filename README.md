# HR AI Project

This is a Spring Boot project that integrates AI capabilities with SQLite database storage and Swagger API documentation.

## Technical Requirements
- Spring Boot
- JDK 21
- Vue3 (frontend framework - to be implemented)
- SQLite database
- Spring Boot AI integration

## Features
1. Store AI model configurations in the database
2. Integrated Swagger for API documentation
3. RESTful APIs for managing AI model configurations

## Project Structure
- `entity/` - JPA entities for database mapping
- `repository/` - Spring Data Jpa repositories for database operations
- `service/` - Business logic layer
- `controller/` - REST API endpoints
- `config/` - Configuration classes

## APIs
- GET `/api/ai-config` - Get all AI model configurations
- GET `/api/ai-config/{id}` - Get AI model configuration by ID
- POST `/api/ai-config` - Create a new AI model configuration
- PUT `/api/ai-config/{id}` - Update an existing AI model configuration
- DELETE `/api/ai-config/{id}` - Delete an AI model configuration
- GET `/api/ai-config/active` - Get all active AI model configurations

## Swagger UI
After starting the application, access the Swagger UI at:
- http://localhost:8080/swagger-ui.html

## Database
The application uses SQLite as the database. The database file `hr_ai.db` will be created automatically when the application starts.