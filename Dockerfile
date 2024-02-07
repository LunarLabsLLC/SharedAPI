FROM openjdk:11-jre-slim AS base

# Install dependencies only when needed
FROM base AS deps
WORKDIR /app

# Copy Gradle files and wrapper
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew* ./
COPY gradle/wrapper ./gradle/wrapper

# Download dependencies
RUN bash ./gradlew --no-daemon dependencies

# Rebuild the source code only when needed
FROM deps AS builder
WORKDIR /app

COPY src ./src

# Build the application
RUN bash ./gradlew --no-daemon shadowJar

# Production image. Copy the necessary files, configures the user, then runs
FROM base AS runner
WORKDIR /app

RUN addgroup --system --gid 1001 kotlin
RUN adduser --system --uid 1001 ktor

# Copy the fat JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar ./app.jar
COPY config-prod.toml config.toml ./

# Ensure the ktor user has write permissions to /app
RUN chown -R ktor:kotlin /app

USER ktor

# Expose the port on which Ktor will run and configures env
EXPOSE 8080
ENV PORT 8080
ENV HOSTNAME "0.0.0.0"

# Command to run the application
ENV DEPLOYMENT_ENV "production"
CMD ["java", "-jar", "app.jar"]
