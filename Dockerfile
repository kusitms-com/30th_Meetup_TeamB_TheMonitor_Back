FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/*.jar /app/the_monitor.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/the_monitor.jar"]