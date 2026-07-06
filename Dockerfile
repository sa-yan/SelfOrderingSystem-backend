FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
# Strip Windows line endings so the wrapper script runs on Alpine
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew bootJar --no-daemon
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
CMD ["java", "-jar", "build/libs/SelfOrderingSystem-0.0.1-SNAPSHOT.jar"]
