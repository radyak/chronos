# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What Chronos is

Chronos is a **structured history database** with query and visualization tooling. It is not a wiki of prose: historical
entities (*entries*) and — equally important — the *relations* between them are modelled as a **graph** with schema-governed,
typed attributes, so that correlations, parallelisms and lineages can be queried and rendered as infographics.

Two audiences are targeted (see `doc/plan/phase 0 - idea.md`): professionals who curate data, and casual users who browse,
query and build graphics. Read access is anonymous; write access requires an OAuth admin role.

### Project stage — read this before designing anything

The app is **pre-release and in build-up**. `doc/plan/phase 0..4` document the reasoning history; `doc/plan/phase 4 - first
release.md` is the current, authoritative direction. The explicit engineering priority is **extensibility and agility over
completeness**: open-closed and clean architecture win over shortcuts, because the past PoCs' "quick fixes" are precisely
what phase 4 is now paying off ("design blots"). Concretely, when adding to this codebase:

- Prefer **new implementations of an existing abstraction** over branching inside existing code (e.g. a new
  `ValidationRule` bean rather than an `if` in `ValidationService`).
- Keep **layers separated**: `rest` (controllers/DTOs) → `service` (domain) → `persistence`/`client` (I/O). Domain services
  must not leak Neo4j/JPA/HTTP types outward. A known outstanding defect from phase 2 is a mix of DTO / domain / "AO"
  objects — do not add to it; prefer mapping at the boundary (`EntryMapper`, `ModelMapper`).
- Anything that will grow (schema types, validation, query filters, chart/pipeline blocks) belongs behind an **extension
  point**, not in a switch.

Decisions already made in phase 4 that constrain design:

| Topic | Decision |
| --- | --- |
| Meta-model vs. schema | The **meta-model is code**; **entity/relation *types* are data** in the Schema Definition Service — extensible without redeployment. |
| Schema evolution | **Additive-only** by default (new optional attributes). Updates/removals/new mandatory attributes need migration and are an extension point. |
| Verifiability (sources/evidence) | *Not* part of the domain model and **no reification of relations**. It becomes an orthogonal sub-set of data nodes (e.g. `_evidence`) carrying `status` / `sources` / `verification`, per relation & entry — never per attribute. |
| Review & approval | **Not in the first release.** Later a separate review service (document DB, revision history, `proposed → in review → approved/published`) covering schema, entries and labels. Creation endpoints would then be locked down to that service — keep them isolated enough to allow it. |
| Fuzzy dates | Move toward **EDTF** rather than a homegrown notation. Today only a narrow subset exists (`ChronosDateSpecValidator`, `AttributeType.DATENOTATION`); calendar systems are an editing/query concern, storage stays on one standard. |
| Query-Transform-Display pipelines | Planned, deliberately shallow v1; typed blocks, versioned additive-only contracts. Nothing implemented yet. |

## Architecture

Quasi-microservices behind a Traefik gateway; every backend is an independent Spring Boot app sharing `chronos-commons`.
`doc/architecture.md` has the diagram.

| Module | Port | Responsibility | Store |
| --- | --- | --- | --- |
| `chronos-frontend` | 4200 (dev) / nginx | Angular SPA, OAuth client | — |
| `chronos-gateway` | 5000 | Traefik routing by path prefix; `/` falls through to the UI | — |
| `chronos-schema-definition-service` (SDS) | 8090 | `/api/schema` — the governed **type registry** (types, attributes, relations) | Postgres + Liquibase |
| `chronos-historical-data-service` (HDS) | 8010 | `/api/data` — entries & relations, list/mesh queries, validation against the schema | Neo4j (Cypher-DSL) |
| `chronos-wiki-service` | 8050 | `/api/wiki` — Wikipedia/Wikidata lookup & summaries | none (cached) |
| `chronos-ui-service` | 8040 | `/api/ui` — public web app config (`app-config.json`) and jumbotron images | filesystem |
| `chronos-commons` | — | shared API models (`model.schema.*`), error handling, JWT auth converter, test JWT support | — |
| `chronos-dev-auth` | 7020 | mock OAuth2 server for local dev only | — |

Key flows:

- **Schema drives data.** HDS holds *no* compiled entity classes: an `Entry` is `labels` + `Map<String,Object> attributes`
  + `_meta`. On create/update, `ValidationService` fetches the type from SDS via `SchemaClient`, merges type attributes with
  the schema's `defaultAttributes`, and applies every `ValidationRule` bean. **Adding a constraint = adding a
  `@Component implements ValidationRule`** — `ValidationService` needs no change (its javadoc lists the still-missing rules:
  `isChangeable`, `valuePattern`, `valueRange`).
- **Query model.** `list` = flat, filtered, sorted, paged entries. `mesh` (POST) = entries *plus* relations, returned as
  `RelationRecord`s that the client dedupes. `CypherService` builds statements with Neo4j Cypher-DSL; `CypherClient` runs
  them; `EntryMapper`/`ResultExtractor` map back to the internal model. Filters live in `model.query.*` and are mirrored 1:1
  in the frontend under `common/model/data/query`.
- **Caching.** SDS caches `allTypes`/`getTypeByKey` and evicts the whole cache on any write (`CachingConfig.CacheNames`).
- **Security.** Standard OAuth2 Authorization Code + PKCE. The SPA is the client; every backend is a resource server.
  `ChronosJwtAuthConverter` (commons) reads roles from `resource_access.${app.auth.client-id}.roles` and the username from
  `${app.auth.principle-attribute}`. Each service protects only its own `/**/admin/**` paths; everything else is anonymous.
  Note the admin role default differs per service (`chronos_client_admin`, `..._schema-admin`, `..._wiki-admin`).

### Frontend structure

Angular 21 (NgModule-based root, standalone components elsewhere), Bootstrap 5 + Bootswatch "minty" theme in
`src/app/theme`, D3 for the network graph, `angular-oauth2-oidc` for auth. Layout: `common/` (reusable components, models,
utils), `general/` (security, web config bootstrapped via `provideAppInitializer`), `modules/public|admin|dev`. The `dev`
route (theme showcase) and the `mock` auth mode exist only in dev builds. There is **no design system or UI kit yet** —
establishing one is an explicit phase 4 goal, so new UI should consolidate on the theme variables rather than add ad-hoc CSS.

## Commands

Backend (Java 17, Maven, run from repo root unless stated):

```bash
mvn clean install                    # build all modules
mvn -B clean test                    # all tests (CI does this)
mvn -pl chronos-historical-data-service -am test                     # one module (-am builds chronos-commons too)
mvn -pl chronos-historical-data-service -am test -Dtest=ValidationServiceTest              # one class
mvn -pl chronos-historical-data-service -am test -Dtest='AdminDataApiCreateEntryIntegrationTest#canCreateMinimalEntry'
mvn -pl chronos-schema-definition-service spring-boot:run -Dspring-boot.run.profiles=no-security,in-memory-persistence
```

Frontend (`chronos-frontend/`, Node 24 per CI):

```bash
npm ci
npm run build            # production build into dist/chronos-frontend
npm run start:dev        # dev server, dev auth mode
npm run start:mock       # mocked auth + proxy.conf.json
npm test                 # Karma/Jasmine
```

Full local cluster (gateway on <http://localhost:5000>, Neo4j browser on 7474, adminer on 7000):

```bash
docker-compose up -d                 # all services, hot-reloaded from the mounted source tree
docker-compose up -d data-db         # just Neo4j, when running HDS from the IDE
bash dev/scripts/cleanup.sh          # remove target/ dirs (uses sudo; container-written files)
```

API docs per service: `/v3/api-docs`, `/swagger-ui/index.html`. Ready-made requests live in `dev/requests/*.http`.

## Operations

- **Config is env-var first** (`APP_*`, `PORT`), with Spring properties as the local-dev fallback; see `doc/deployment.md`
  for the auth and DB variables.
- **Spring profiles** (dev/test only): `no-security`, `dev-security` (JWKS from the mock server), `test-security`/`test`
  (self-signed JWTs from `chronos-commons` `dev-jwt-*.key`), `local-persistence`, `in-memory-persistence`, `test-data`,
  `debug`, `prepare-migration`, `dev-*` (git-ignored). `inContainer` is a *Maven* profile that redirects `target/` so
  container and host builds don't collide.
- **SDS DB migrations are Liquibase, never `ddl-auto`** (`ddl-auto=validate` in production config). The generation
  procedure — run with `prepare-migration`, then `mvn liquibase:generateChangeLog`, review, commit under
  `src/main/resources/db/changelogs/` — is documented in `chronos-commons/README.md`.
- **Testing.** Integration tests extend a per-service `BaseIntegrationTest` (MockMvc + `TestJwtGenerator` + `TestDataManager`
  seeding/cleanup). HDS ITs spin up a **Neo4j Testcontainer** per test class (`@Container @ServiceConnection`) and mock
  `SchemaClient` with fixtures from `src/test/resources/mock-responses/`, i.e. HDS tests never need SDS running. JaCoCo runs
  in the `test` phase for every backend module.
- **CI/CD.** `.github/workflows/build.yml` builds frontend and backend, then builds one Docker image per service
  (`radyak/<service>`); images are pushed to Docker Hub **only on a GitHub release** (secret `DOCKERHUB_TOKEN`).

## Conventions

- Branches `feature/GH-<issue>_Short-description`; commits `type(GH-<issue>): Message` (`feat`, `fix`, `doc`, `chore`;
  `No-Issue` when there is none). Work is issue-driven with PRs into `main`.
- Java packages under `net.fvogel.chronos.<service>`; Lombok `@Data` on models; field `@Autowired` is the prevailing style.
  SDS uses a `domain/<aggregate>/{business,persistence,rest,service}` layout with `*PO` persistence entities — that is the
  cleaner reference layout; HDS still uses a flatter `model/service/rest/persistence` split.
- Document non-obvious decisions in `doc/` (`doc/issues.md` for defects with no GitHub issue, `doc/plan/` for direction).

## Known stale/rough spots (don't trust blindly)

- `chronos-commons/README.md` and `chronos-schema-definition-service/README.md` are **swapped** (each describes the other).
- `doc/development.md` still describes the old monolith setup (frontend WebJar as a backend Maven dependency, one shared
  Neo4j) — no backend depends on `chronos-frontend` any more; it is packaged as its own nginx image.
- `doc/deployment.md` describes Postgres as *the* datastore; that applies to SDS only, HDS is Neo4j.
- `doc/structure.md` is an empty stub. `chronos-frontend/proxy.conf.json` targets `chronos-sds`/`chronos-hds`, which are
  not the container names in `docker-compose.yaml` (`schema`/`data`), and `docker-compose.dev-cluster.yaml` still deploys
  the old single `radyak/chronos` monolith image against a Neo4j container configured with Postgres volumes.
- `chronos-historical-data-service/pom.xml` excludes `net/fvogel/chronos/data/REFACTORING/**` from JaCoCo; that package no
  longer exists.
