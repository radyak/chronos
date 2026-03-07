FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY ./target/chronos-ui-service-*.jar app.jar
CMD ["java", "-jar", "app.jar"]