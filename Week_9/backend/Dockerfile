# Base image for the build stage: Maven with Java 17, used to compile the Spring Boot app.
FROM maven:3.9-eclipse-temurin-17 AS build

# Set the working directory inside the builder container.
WORKDIR /workspace

# Copy only the Maven project metadata first to leverage Docker layer caching.
COPY pom.xml .

# Copy the application source code into the build environment.
COPY src ./src

# Compile the project and produce the JAR without running unit tests.
RUN mvn -B -DskipTests package

# Runtime stage: use a lightweight Java 17 JRE image for the final container.
FROM eclipse-temurin:17-jre

# Set the application directory for the runtime image.
WORKDIR /app

# Create the logs directory and assign ownership to a non-root user.
RUN mkdir -p /app/logs && chown -R 10001:0 /app

# Copy the built JAR from the build stage into the runtime image.
# The --chown flag ensures the jar is owned by the runtime user.
COPY --from=build --chown=10001:0 /workspace/target/*.jar /app/app.jar

# Run the app as a non-root user for improved container security.
USER 10001

# Expose the application port expected by Spring Boot.
EXPOSE 8080

# Start the Java application when the container launches.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]