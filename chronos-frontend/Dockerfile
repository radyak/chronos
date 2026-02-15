FROM node:21.7.2-alpine3.18 AS build

ARG APP_VERSION

WORKDIR /build
COPY ./package*.json ./
RUN npm ci
COPY . ./
RUN npm version ${APP_VERSION}; exit 0
RUN npm run build


FROM nginx:latest AS runtime

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build/dist/chronos-frontend/* /usr/share/nginx/html

