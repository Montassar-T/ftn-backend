FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy Maven build files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (caches layers)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
