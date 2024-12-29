# Use the official OpenJDK 21 image as a base image
FROM openjdk:21-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml ./
COPY mvnw ./
COPY mvnw.cmd ./
COPY .mvn ./.mvn
RUN ./mvnw dependency:go-offline -B

# Copy the source code into the container
COPY src ./src

# Build the application using Maven
RUN ./mvnw clean package -DskipTests

# Use a smaller OpenJDK image for running the application
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the build image to the runtime image
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for the application to be accessible
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]