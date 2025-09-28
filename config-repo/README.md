# Blog Platform Configuration Repository

This repository contains centralized configuration files for all microservices in the Blog Platform ecosystem.

## Services Configuration

### 1. Notification Service (`notification-service`)

The notification service handles all notification-related functionality including email notifications, Kafka message processing, and user engagement tracking.

#### Configuration Files:
- `notification-service.properties` - Base configuration
- `notification-service-dev.properties` - Development environment overrides
- `notification-service-prod.properties` - Production environment overrides

#### Key Features:
- **Email Notifications**: Gmail SMTP integration for sending notifications
- **Kafka Integration**: Consumer and producer configurations for event-driven notifications
- **Eureka Service Discovery**: Automatic registration and discovery
- **Health Monitoring**: Comprehensive health checks and metrics
- **Rate Limiting**: Configurable email rate limiting
- **Retry Logic**: Configurable retry attempts for failed operations

#### Environment Variables Required:
- `MAIL_USERNAME`: Gmail username for SMTP
- `MAIL_PASSWORD`: Gmail app password for SMTP
- `MAIL_HOST`: SMTP host (default: smtp.gmail.com)
- `MAIL_PORT`: SMTP port (default: 587)

#### Kafka Topics:
- `blog-created`: New blog post notifications
- `blog-updated`: Blog post update notifications
- `blog-deleted`: Blog post deletion notifications
- `comment-added`: New comment notifications
- `user-registered`: User registration notifications

#### Port Configuration:
- **Development**: 8084
- **Production**: 8084

#### Health Check Endpoints:
- Health: `http://localhost:8084/actuator/health`
- Info: `http://localhost:8084/actuator/info`
- Metrics: `http://localhost:8084/actuator/metrics`

## Configuration Management

### Config Server Integration
The notification service is fully integrated with the Spring Cloud Config Server for centralized configuration management.

### Service Discovery
All services register with Eureka Server for automatic service discovery and load balancing.

### Environment-Specific Configuration
- **Development**: Optimized for local development with debug logging
- **Production**: Optimized for production with performance tuning and security

## Usage

### Starting the Notification Service
1. Ensure Eureka Server is running on port 8761
2. Ensure Config Server is running on port 8088
3. Ensure Kafka is running on localhost:9092
4. Set required environment variables
5. Start the notification service

### Configuration Updates
Configuration changes can be made by updating the respective properties files in this repository. The config server will automatically refresh configurations for running services.

## Monitoring and Observability

### Health Checks
- Service health status
- Database connectivity
- Kafka connectivity
- Email service connectivity

### Metrics
- Email send rates
- Kafka message processing rates
- Error rates
- Response times

### Logging
- Structured logging with configurable levels
- Environment-specific log configurations
- Request/response logging

## Security Considerations

### Production Environment
- Use environment variables for sensitive configuration
- Enable rate limiting
- Configure proper logging levels
- Use secure SMTP configurations

### Development Environment
- Rate limiting disabled for testing
- Debug logging enabled
- Test SMTP configuration available

## Troubleshooting

### Common Issues
1. **Service Registration Issues**: Check Eureka Server connectivity
2. **Configuration Loading Issues**: Check Config Server connectivity
3. **Email Sending Issues**: Verify SMTP credentials and configuration
4. **Kafka Issues**: Check Kafka broker connectivity and topic configuration

### Debug Mode
Enable debug logging by setting the appropriate log levels in the configuration files.

## Support

For issues related to configuration management, please refer to the Spring Cloud Config documentation or contact the development team.
