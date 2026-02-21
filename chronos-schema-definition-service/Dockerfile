FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY ./target/chronos-schema-definition-service-*.jar app.jar
CMD ["java", "-jar", "app.jar"]