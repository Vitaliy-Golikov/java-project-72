FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY app/ /app/
RUN ./gradlew build --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 7070
CMD ["java", "-jar", "app.jar"]