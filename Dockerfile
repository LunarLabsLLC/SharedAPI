FROM openjdk:11-jre-slim AS base

# Install dependencies only when needed
# Use the official OpenJDK base image
FROM base AS builder
WORKDIR /app

# Copy Gradle files and wrapper
COPY ./build.gradle.kts ./settings.gradle.kts ./gradlew* ./
COPY ./gradle/wrapper ./gradle/wrapper

# Download dependencies
RUN ./gradlew --no-daemon dependencies

# Copy the application source code
COPY ./src ./src

# Build the application
RUN ./gradlew --no-daemon shadowJar

# Production image, copy all the files and run next
FROM base AS runner
WORKDIR /app

RUN addgroup --system --gid 1001 kotlin
RUN adduser --system --uid 1001 ktor

# Copy the fat JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar ./app.jar

USER ktor

# Expose the port on which Ktor will run
EXPOSE 8080
ENV PORT 8080
ENV HOSTNAME "0.0.0.0"

# Command to run the application
CMD ["java", "-jar", "app.jar"]
