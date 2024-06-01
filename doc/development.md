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

## Profiles
The following Spring profiles are intended to be used *only for development*:

### `test-persistence-inmemory`
Activates H2 database in in-memory mode for development.
H2 Console: see /h2-console (JDBC String: `jdbc:h2:mem:chronos`)

### `test-persistence-filebased`
Activates H2 database in file-based mode for development.
H2 Console: see /h2-console (JDBC String: `jdbc:h2:mem:chronos`)

### `test-persistence-postgres`
Configures a connection with the Postgres database in the provided [docker-compose.yaml](../docker-compose.yaml)

### `test-data`
Sets up some test data.
PLEASE NOTE: This does currently *not* work with Postgres (reason unknown yet). 

### `no-liquibase`
Disables Liquibase for development, e.g. to let auto-ddl update the database schema automatically, in order to generate the next Liquibase changelog.

### `no-security`
Disables security.

### `dev-*`
Custom profile(s) for development; application properties are git-ignored.


## Database

The database schema is versioned with [Liquibase](https://www.liquibase.com/).


### Generating changelogs

With the current configuration in place, it is recommended to proceed as described below, to add a new changelog:

1. Checkout the *main* branch (or a respective Git revision with the previous database state).
2. Start
   * the local Postgres instance from the [docker-compose.yaml](../docker-compose.yaml)
   * the [chronos-backend](../chronos-backend) with the profiles `test-persistence-postgres` (+ optionally `test-data`)
3. Execute the command `mvn liquibase:generateChangeLog`. This generates a full `changelog.xml` in `src/main/resources/db/changelog`, which should be renamed to `changelog_old.xml` or something similar.
4. Cheout the *feature/...* branch (or a respective Git revision with the new database state).
5. Start the chronos-backend again, this time with the profiles `no-liquibase` (to let JPA's auto DDL do the update) and again with `test-persistence-postgres`.  
6. Again, execute the command `mvn liquibase:generateChangeLog`, to generate a new full `changelog.xml` in `src/main/resources/db/changelog`.
7. Compare the two generated changelogs, collect the delta in a new changelog and reference it in the `master.xml`.
8. To verify the proper functioning of the changeset, proceed as follows:
   1. If started, stop the app and stop and dispose the Postgres container
   2. Delete the Postgres data (by default: `/dev/chronos-postrest-data`)
   3. Perform steps 1-4 again.
   4. Start the chronos-backend again, this time only with the profile `test-persistence-postgres`.
   5. The startup should not throw any errors, as both Liquibase run + JPA validates the schema.

### Conventions

* The naming pattern for changelog files is:
  > chronos-db-v[*version*]_[*short kebab-case description*].xml
* IDs of changesets can keep their generated timestamp string, but the counter postfix should be adapted to be consistent.
* The generated author of changesets should be changed, if necessary.
