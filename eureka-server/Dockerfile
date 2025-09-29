# Simple build using pre-compiled JAR
FROM eclipse-temurin:24-jre

# Set working directory
WORKDIR /app

# Copy the pre-built JAR file directly
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
