# Use a base image with Java and Maven pre-installed
FROM eclipse-temurin:17-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give execute permissions to Maven wrapper
RUN chmod +x mvnw

# Download dependencies (without running tests)
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src/ src/

# Build the application (without tests)
RUN ./mvnw clean package -DskipTests

# Use a smaller base image for the final container
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the previous build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]








