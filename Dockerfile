FROM node:21.7.2-alpine3.18 as frontend-build

ARG APP_VERSION

WORKDIR /build
COPY ./chronos-frontend/package*.json ./
RUN npm install
COPY chronos-frontend ./
RUN npm version ${APP_VERSION}; exit 0
RUN npm run build
# -> artefacts in /build/dist/chronos-frontend


FROM maven:3.9.6-eclipse-temurin-17-alpine as backend-build

WORKDIR /build
COPY . .
COPY --from=frontend-build /build/dist/ ./chronos-frontend/dist/
RUN mvn clean package


FROM eclipse-temurin:17-jre-alpine as runtime

WORKDIR /app
COPY --from=backend-build /build/chronos-backend/target/chronos-backend-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]