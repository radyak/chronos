# Development

## Prerequisites
The aspects of the application have different technical requirements:
* Backend:
  * Java SDK: 17
  * Maven: 3.8.7
* Frontend:
  * NPM: 9.2.0
  * Node: 16.19.0
  * Angular CLI (optional): see [package.json](../chronos-frontend/package.json)
* Container:
  * Docker: 25.0.2
  * Docker-compose: 2.24.5


## Setup
1. From `/chronos-frontend`: run `npm install && npm run build`. This will generate initial static resources for the WebJar in step 2.
2. From `/` (project root): run `mvn clean install`. This will also create a WebJar, that fulfills the `chronos-frontend` mvn dependency of the backend.

TODO: Describe docker-compose, custom dev profile, img path


## Run

### Backend
Before starting the backend, the Neo4J container must be started. This can be done with the command
```bash
docker-compose up -d chronos-db
```

The backend comes with the Maven Spring-Boot plugin, which allows the backend to be started with the command:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=no-security,local-persistence
```
For info about the profiles, see below.


## Profiles
The following Spring profiles are intended to be used *only for development*:

### `test-data`
Sets up some test data.
PLEASE NOTE: This is currently just a dummy, it does not yet import anything

### `local-persistence`
Sets up connection to a locally running Neo4j instance, defined in the [docker-compose.yaml](../docker-compose.yaml).

### `debug`
Set some properties, such as log levels, for debugging.

### `no-security`
Disables security.

### `dev-*`
Custom profile(s) for development; application properties are git-ignored.