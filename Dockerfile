FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY app/ /app/
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 7070
CMD ["java", "-jar", "app.jar"]