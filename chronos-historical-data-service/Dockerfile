FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY ./target/chronos-historical-data-service-*.jar app.jar
CMD ["java", "-jar", "app.jar"]