FROM openjdk:22-jdk-slim

LABEL maintainer="geekysayan@gmail.com"

WORKDIR /app

COPY build/libs/SelfOrderingSystem-0.0.1-SNAPSHOT.jar /app/self-ordering-system.jar

ENTRYPOINT ["java", "-jar", "/app/self-ordering-system.jar"]
