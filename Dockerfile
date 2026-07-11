FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY app/ /app/
RUN chmod +x gradlew
RUN ./gradlew shadowJar --no-daemon

# Используем JDK вместо JRE
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
EXPOSE 7070
CMD ["java", "-jar", "app.jar"]