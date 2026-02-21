# Schema Definition Service

## Development

Start service via local maven: `mvn spring-boot:run -Dspring-boot.run.profiles=...`

### Specific Profiles

The following Spring profiles are intended to be used *only for development* and are *specifically supported by the
*Schema Definition Service*:

#### `debug`

Set some properties, such as log levels, for debugging.

#### `dev-security`

Activates components and configs to generate and validate JWTs locally.

#### `in-memory-persistence`

Sets up and connects to an in-memory H2 database.

#### `local-persistence`

Sets up connection to a locally running Postgres instance, defined in the [docker-compose.yaml](../docker-compose.yaml).

#### `no-security`

Disables security.

#### `prepare-migration`

Special profile to prepare setting up a Liquibase migration (see "Database Migrations")

#### `test-data`

Sets up some test data.

### Database Migrations

Changes to the DB schema need to be automized with Liquibase.

The procedure is based on letting the Liquibase Maven plugin compare a locally running Postgres database against the
current set of changelogs:

1. Implement changes to JPA entities as needed.
1. Start a local Postgres DB with Docker - see
    * `chronos-sds` in `docker-compose.dev.yaml`
    * `application-local-persistence.properties`
1. Start the application with profile _prepare-migration_ - this should let Hibernate try to update the local DB schema
   on its own.
1. Update `diffChangeLogFile` in `pom.xml` to the latest changelog
1. Optional: Update `outputChangeLogFile` in `pom.xml` to an appropriate new changelog name
1. Execute `mvn liquibase:generateChangeLog` - this should generate the latest changelog
1. Review the generated changelog (e.g. rename generic constraint names), move/rename it as necessary and commit it

NOTE: After generating the changelog, the application **cannot** start with a Liquibase migration, as its ddl-auto has
already updated it!