# Starting with this Linux server
FROM maven:3-eclipse-temurin-21 AS builder

## Build the application

WORKDIR /app

# everything after this is in /app
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

# Build the application
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-bullseye

WORKDIR /app 

COPY --from=builder /app/target/assessment-0.0.1-SNAPSHOT.jar paf.jar

## Run the application
# Define environment variable 
ENV PORT=8080

# Expose the port
EXPOSE ${PORT}

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar paf.jar