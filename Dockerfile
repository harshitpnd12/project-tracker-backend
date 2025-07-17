# Use OpenJDK 21
FROM openjdk:21-jdk-slim

# Add a volume to store temp files
VOLUME /tmp

# Copy the JAR file built by Maven
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java","-jar","/app.jar"]
