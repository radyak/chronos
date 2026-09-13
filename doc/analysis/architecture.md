# Architecture Review — Clean Architecture, Extensibility, Consistency

*Date: 2026-09-13 · Branch: `feature/GH-0_Prepare-AI` · Scope: all modules (backends, commons, frontend, gateway, build, CI, docs)*

This review measures the codebase against the priorities stated in `CLAUDE.md` / phase 4: **layer separation**, **open-closed / extension points**, **consistency of project and module structure**, plus best-practice violations encountered along the way. Aspects not yet addressed by the project (observability, scalability, …) are only sketched at the end.

Findings were collected by four parallel focused reviews (HDS; SDS + commons + wiki + ui + dev-auth; frontend; build/infra/security/ops) and consolidated here; overlaps were merged. All findings were derived from reading the code; the high-severity ones were additionally spot-checked. Items marked *(verify)* rely on external facts (versions/support dates) that should be double-checked.

**Legend**

* Severity: 🔴 high · 🟠 medium · 🟢 low
* Path prefixes:
  * `hds/` = `chronos-historical-data-service/src/main/java/net/fvogel/chronos/data/`
  * `sds/` = `chronos-schema-definition-service/src/main/java/net/fvogel/chronos/schema/domain/schema/`
  * `commons/` = `chronos-commons/src/main/java/net/fvogel/chronos/commons/`
  * `wiki/` = `chronos-wiki-service/src/main/java/net/fvogel/chronos/wiki/`
  * `ui-svc/` = `chronos-ui-service/src/main/java/…`
  * `fe/` = `chronos-frontend/src/app/`

---

## 0. Executive summary — top priorities

| # | Topic | Finding | Ref |
|---|---|---|---|
| 1 | Security | Dev RSA **private key + JWT minting beans ship in the main jar** of every service image; one profile (`test`/`test-security`) makes a service trust self-minted admin tokens (and it logs ~100-year admin JWTs). A `no-security` profile likewise opens admin endpoints. | [S-1], [S-2] |
| 2 | Extensibility / data integrity | **Additive-only schema evolution is not enforced** — PUT on a type can remove attributes or change types; a test even asserts removal works. | [E-1] |
| 3 | Layering | **SDS binds JPA `*PO` entities directly to REST** (clients can set ids → re-parent attributes of other types); **HDS `service` package is really persistence** (Cypher-DSL, Neo4j driver types) and the internal `Entry` is the API model. | [L-1], [L-2], [L-3] |
| 4 | Correctness | HDS writes are **not transactional** (raw auto-commit sessions despite `@Transactional`); create accepts client `elementId`/`_meta` (duplicate keys possible); missing mandatory attribute → **500** (NPE in `Map.of`). | [C-1]–[C-4] |
| 5 | Correctness (FE) | Frontend **discards edits of type-specific attributes** on save; **cancelling a delete still navigates away**; admin HTTP interceptors registered on a route **never run**. | [C-10]–[C-12] |
| 6 | Extensibility | `AttributeType` is a **closed enum in the shared wire model**, handled by if-chains/switches in HDS validation, HDS mapping, and several frontend templates. Adding a type = coordinated release of 4 modules. | [E-2] |
| 7 | Resilience | Inter-service/external HTTP clients have **no timeouts**, no error mapping, no caching (HDS→SDS on every write; wiki→Wikipedia). | [R-1] |
| 8 | Testing | SDS tests run on **H2 with Liquibase disabled** (`ddl-auto=update`) — changelogs never exercised; **frontend tests never run in CI** and are broken boilerplate. | [T-1], [T-4] |
| 9 | Build | Stack age: Spring Boot 3.5 / Java 17 / springdoc 2.2.0 (likely incompatible with Boot 3.5) *(verify)*; HDS doesn't inherit the root POM; heavy POM duplication. | [B-1]–[B-4] |
| 10 | Ops basics | **No actuator/health endpoints** anywhere; Traefik dashboard `insecure: true`; no healthchecks in compose/Docker. | [O-1], [S-6] |

Suggested order: security quick fixes (S-1, S-2, S-3, S-6) → data-integrity fixes (C-1..C-4, E-1, L-1) → frontend correctness (C-10..C-12) → structural refactorings (HDS layout, contracts module, attribute-type SPI) → test infrastructure → build/ops hygiene.

---

## 1. Layering & separation of concerns

### Backend

- **[L-1] 🔴 SDS: JPA entities are the REST contract.** `sds/rest/AdminSchemaController.java:22,36` binds `TypePO`/`RelationPO` directly; POs carry Jackson annotations (`TypePO.java:64`). Clients can send attribute/relation `id`s — sending another type's attribute id makes `typeAttributePORepository.save` update and re-parent that row. `TestDataManager` also deserializes JSON straight into POs.
  → Introduce request/response DTOs without ids/back-references (`TypeDefinitionRequest`), a domain model in `business` (`SchemaType`, `AttributeDefinition`), and a PO mapper in `persistence` that resolves attributes by `(type, key)`, never by client id.
- **[L-2] 🔴 HDS: the `service` package is persistence.** `hds/service/CypherService.java`, `CypherDslUtils.java` build Cypher; `hds/service/EntryMapper.java:7-12` imports Neo4j driver `Node/Record/Value` and even `org.neo4j.driver.internal.types.InternalTypeSystem`; `persistence/ResultExtractor` exposes driver `Result`; `CypherClient` takes Cypher-DSL `Statement`. `DataService` is a near pass-through.
  → Adopt the SDS reference layout `domain/entry/{business,service,persistence,rest}`; define a port (`EntryRepository`) in the domain with a `Neo4jEntryRepository` adapter; `DataService` only sees domain types.
- **[L-3] 🟠 HDS: domain model = API model; logic in controllers.** Internal `Entry` is request and response body (`hds/rest/AdminDataController.java:33,38`); `DataResponseDTO` embeds `Entry`, `Relation`, `BaseQuery`; mesh de-duplication and query assembly live in `hds/rest/DataController.java:46-55,88-96`; `CountResultDTO` is produced by persistence (`CypherService:173`, `EntryMapper:85`).
  → Request/response DTOs + mapper in `rest`; service returns a `MeshResult`; persistence returns a domain `LabelCount`.
- **[L-4] 🟠 HDS validation rules depend on the query builder.** `IsUniqueValidationRule.java:24`, `IsChangeableValidationRule.java:22` inject `CypherService`. → Rules depend on the repository port plus a `ValidationContext` (see [E-4]).
- **[L-5] 🟠 Shared API DTOs double as SDS domain model.** `commons/model/schema/*` (`SchemaResponse`, `Type`, `Attribute`, `Relation`) is the HDS↔SDS wire contract *and* is used inside SDS business code (`DefaultTypeAttributesRule` holds `Set<Attribute>`).
  → Extract a versioned `chronos-schema-api` contract module (DTOs only); SDS gets its own domain model and maps at the edge.
- **[L-6] 🟠 SDS REST mapper contains business/query logic.** `sds/rest/mappers/ModelMapper.java:20-21,91-110` injects `DefaultTypeAttributesRule` and decides graph expansion (inbound/outbound relations, neighbour types); controllers repeat `meta.base/depth` assembly three times (`SchemaController.java:25-50`, `AdminSchemaController.java:21-50`).
  → `SchemaService.getSchema(key, depth)` returns a domain view; mapper only converts.
- **[L-7] 🟠 HTTP semantics leak into domain exceptions.** `commons/exception/*.java` carry `@ResponseStatus`; HDS `SchemaValidationException.java:11` too; SDS `business/ValidationException.java:11` is annotated `BAD_GATEWAY` (502 — plain wrong); `ValidationError` carries Jackson annotations; persistence code throws them. → Framework-free exceptions, mapped only in the `@RestControllerAdvice`.
- **[L-8] 🟢 Misplaced dependencies.** SDS `DefaultTypeAttributesRule.java:7,46` loads resources via a *dev config* class' classloader; SDS entities import `config.i18n.I18nConstants`; ui-service `AuthConfig` (`@ConfigurationProperties`) is a `@Configuration` in the `model` package, `WebAppConfig` a private record inside a controller; HDS `EntryFilterExtractor` is a `@Service` in `rest`; wiki `WikipediaModelMapper` is a `@Service` with only static methods.

### Frontend

- **[L-9] 🟠 Inverted dependencies `common` → features.** `fe/common/components/wiki-article-input/wiki-article-input.component.ts:9,11` imports `modules/admin/clients/…` and `modules/public/services/…`; admin reaches into `modules/public/{clients,services}`; "public" code imports `model/schema/admin/type.ao`.
  → Split into `core/api` (clients + DTOs), `core/domain` (models + mappers), `features/{public,admin}`; enforce with ESLint `import/no-restricted-paths` or Sheriff. Abstract ports (injection tokens) where `common` components need feature services.
- **[L-10] 🟠 Components/validators bypass the client layer.** `fe/common/validators/api-unique-validator.ts:31-33` calls `HttpClient` with an unencoded, string-interpolated URL (sends literal `elementId=null`). → `AdminDataClient.isUnique(…)` with `HttpParams`.
- **[L-11] 🟠 DTO/AO duplication continues in the frontend.** `fe/common/model/schema/admin/*.ao.ts` copy the DTOs field-by-field with everything optional; `AttributeMapper` is an identity copy; `ErrorResponseDTO` is an `HttpErrorResponse` cast. This extends the "DTO/domain/AO mix" defect `CLAUDE.md` warns about. → Generated DTOs at the client boundary ([X-1]), real domain models (non-optional where guaranteed), an `ApiError` mapper in an interceptor.
- **[L-12] 🟠 Business logic in components.** Entry form ↔ model mapping in `edit-entry.component.ts:188-196` (buggy, see [C-10]); `valueRange` parsing in `entry-attribute-form.service.ts:36-43`. → Tested mappers/parsers (`EntryFormMapper`).

---

## 2. Extensibility / open-closed

- **[E-1] 🔴 Additive-only schema evolution is not enforced.** `sds/service/SchemaService.java:67-88` + `AdminSchemaController.java:35-50`: PUT replaces the whole type — removing attributes/relations, changing an attribute's `type`, toggling `isMandatory`/`isUnique`/`allowedValues` all succeed; `AdminApiTypeUpdateIntegrationTest.java:170-195` (`canRemoveAttributeFromExistingType`) asserts removal. Existing HDS data can become silently invalid.
  → Extension point `SchemaChangePolicy`: compute a `SchemaDiff(old, new)` and run `List<SchemaChangeRule>` beans (`NoAttributeRemovalRule`, `NoTypeChangeRule`, `NewAttributesMustBeOptionalRule`, …). Non-additive changes later unlocked by a `MigrationStrategy` bean — exactly the phase-4 decision.
- **[E-2] 🔴 Attribute types are a closed enum handled by switches in many places.** `commons/model/schema/AttributeType.java` is compiled into the shared wire model and SDS persistence (`TypeAttributePO.java:41`). It is branched on in:
  - HDS `IsCorrectTypeValidationRule.java:49-54,65-87` (if-chain, ENUM unchecked, arrays not checked element-wise)
  - HDS `EntryMapper.java:94-115` (switch on driver type-name strings)
  - FE `dynamic-input.component.html:2-52` (`@switch`), `edit-attribute-dialog.component.ts:43-54` + `.html:129,156,176,196` (`isType(...)`, `!== WIKIQID`)
  - FE `entry-attribute-form.service.ts` (validators)

  Consumers don't tolerate unknown enum values (no `READ_UNKNOWN_ENUM_VALUES_AS_*`), so even additive types break older HDS builds.
  → **Backend:** `AttributeTypeHandler` SPI keyed by string id (one bean per type: validate, coerce/convert for filters, map from store); type carried as string on the wire. **Frontend:** `ATTRIBUTE_TYPE_HANDLERS` multi-provider registry `{ editor component, constraint fields, validatorsFor(attr), selectableInSchemaEditor }`, rendered via `NgComponentOutlet`. This is the single most valuable extension point given the "types are data" decision.
- **[E-3] 🟠 HDS query filters/operators are switch-based and conflated.** `hds/service/CypherDslUtils.java:46-58,85-123` hard-codes operators; `EntryFilter` mixes label and attribute criteria — with `labels` set, attribute conditions are silently dropped (locked in by `searchMeshLabelsInSameFilterHaveHigherPriority…`). Mesh uses only the first `RelationFilter`'s shape but ANDs all filters' attribute conditions onto it (`CypherService.java:92-109`).
  → `ConditionOperator` with abstract `toCondition(...)` or `OperatorTranslator` beans; polymorphic filters (`LabelFilter`, `AttributeFilter` via `@JsonTypeInfo`) with a `FilterTranslator<F>` registry. Reject >1 relation filter (400) until supported.
- **[E-4] 🟠 HDS `ValidationRule` contract is too thin.** Rules guess CREATE vs UPDATE from `elementId`, re-query the existing entry (`IsChangeable` duplicates `DataService.update`), and each rule re-does a linear attribute lookup (`IsAllowedValues:27-32`, `IsArray:25-28`, `IsCorrectType:40-43`, `IsDefinedAttribute:21-24`). `IsChangeable` throws mid-validation (`:32`) instead of returning an error.
  → Pass a `ValidationContext { operation, existingEntry, effectiveType (Map<String,Attribute>) }` built once by `ValidationService`.
- **[E-5] 🟠 SDS schema-definition validation is one monolithic class.** `sds/business/UniqueValidator.java:14-114` is called by hand; duplicate-key loops are copy-pasted 3×; missing checks: ENUM ⇔ `allowedValues`, `valuePattern` compiles, `valueRange` syntax, relation target exists, relation key unique per source, reserved default keys.
  → Mirror HDS: `SchemaDefinitionRule` interface + injected list.
- **[E-6] 🟠 Duplicate attribute definitions in SDS.** `TypeAttributePO` and `RelationAttributePO` are 100% duplicates (82 lines each) incl. builders and `ModelMapper.java:35-89` overloads — each new attribute flag touches ~6 places + 2 changelogs. → `@MappedSuperclass AttributeDefinitionPO` (or one table with owner discriminator) and a single mapper.
- **[E-7] 🟠 Error handling is not pluggable.** Commons `RestExceptionHandler` + subclasses both annotated `@RestControllerAdvice` without `@Order` (`commons/rest/exceptionhandling/RestExceptionHandler.java:24`, HDS/SDS subclasses `:20`) — which one handles an exception depends on registration order. → Abstract non-annotated base or `@Order`, better: one advice plus `ExceptionMapper` beans. FE counterpart: closed `ERROR_MAP` in `fe/…/backend-error.service.ts:9-15` ("To be continued …") → multi-provider message templates keyed by backend `org.chronos.*` codes.
- **[E-8] 🟢 Magic values instead of explicit modes.** HDS `sortBy=random`, `"null"` string in `EntryFilterExtractor`; FE hand-writes `"wikiqid:not": "null"` (`wiki-article.service.ts:24`). → Explicit enum/sort mode, typed query serializer.

---

## 3. Project & module structure / consistency

- **[X-1] 🟠 API contracts are hand-mirrored — and already drifted.** FE `common/model/data/query/list-query.model.dto.ts` is flat (`page/pageSize/sortBy/sortOrder`) while HDS `ListQuery` has `entryFilters`, `pagination{}`, `sorting[]`; `FilterOperator` ≠ `ConditionOperator` (`not/gt/has` missing); `'asc'` vs `ASC`; `historical-data.client.ts:19` casts params `as any`. `CLAUDE.md`'s "mirrored 1:1" claim is not true.
  → Generate TS models from each service's `/v3/api-docs` (`openapi-typescript` / `ng-openapi-gen`) as a build step; typed `ListQuery → HttpParams` serializer. Pairs well with [L-5] (contract module) on the Java side.
- **[X-2] 🟠 Inconsistent backend package layouts.** SDS: `domain/<aggregate>/{business,persistence,rest,service}`; HDS: flat `model/service/rest/persistence/exception/utils` with only `shared/rest/errorhandling` aligned; wiki: flat `client/dto/mapper/model/rest/service` where "dto" means *Wikipedia* payloads (elsewhere "dto" = our API). HDS main class `ChronosBackendApplication` and pom name `chronos-backend` are monolith leftovers.
  → Converge on the SDS layout for all services; name external payloads `Wikipedia*Response`; rename HDS artifacts.
- **[X-3] 🟠 `chronos-commons` is an overweight shared kernel.** It forces web, security, OAuth2 resource server, JOSE and cache on every consumer (`chronos-commons/pom.xml:26-49`) and is wired via `@ComponentScan("net.fvogel.chronos.commons")`; ui-service needs dummy `app.auth.principle-attribute=` / `admin-role=` (`application.properties:8-9`). It also contains test-only code ([S-1]) and a wiki-only `SupportedLanguage`.
  → Split into `chronos-schema-api` (contracts), `chronos-commons-web` (error model/handling), `chronos-commons-security` (converter + shared `SecurityFilterChain` factory) as **Spring Boot auto-configurations** (`AutoConfiguration.imports`, `@ConditionalOnProperty`, `@ConfigurationProperties("app.auth")`), and `chronos-test-support` (test scope).
- **[X-4] 🟠 Security configuration drifts between services.** Public paths use `.anonymous()` in HDS (`config/security/SecurityConfig.java:37`), wiki (`:31`), ui-service (`:23`) but `.permitAll()` in SDS (`:46`). **`anonymous()` rejects requests that carry a valid bearer token with 403** — latent only because the SPA attaches tokens exclusively to `/admin` URLs. CSRF disabled everywhere except wiki (`:27`); `http.cors(withDefaults())` without a `CorsConfigurationSource` (no-op); `NoSecurityConfig` duplicated in 3 services, absent in ui; admin role defaults differ per service while dev-auth mock only emits `chronos_client_admin` (compose overrides hide it).
  → One shared, parameterised filter chain in commons-security (`permitAll`, CSRF off, admin path prefix from config); dev-auth mock emits all roles.
- **[X-5] 🟠 Frontend: mixed bootstrap and API styles.** NgModule root (`main.ts:9` `bootstrapModule`, `app.component.ts:9` `standalone:false`, `app-routing.module.ts`, `dev.module.ts`) vs standalone elsewhere; constructor DI vs `inject()`; `@Input/@Output` vs `input()/output()`; RxJS `ReplaySubject` in `AuthService` vs signals elsewhere; deprecated `BrowserAnimationsModule` (unused), `.toPromise()`.
  → `bootstrapApplication` + `provideRouter/provideHttpClient(withInterceptors)`; standardise on `inject()`/signal inputs; enforce with `@angular-eslint` (no ESLint configured today).
- **[X-6] 🟠 Frontend feature loading.** Admin area is eagerly imported (`fe/app-routing.module.ts:12-20`, `loadChildren` commented out), pulling the whole FontAwesome solid pack (`admin-icons.service.ts:26-27`, reading private `(faLib as any).definitions`) into the anonymous bundle; dev showcase chunk (3.8k lines + `ngx-colors`) is still built into prod `dist` (runtime `isDevMode()` check); `AuthServiceMock` statically imported into prod. → Lazy `loadChildren` + `canMatch`; dev routes and auth mock via environment file replacement behind an abstract `AuthPort`.
- **[X-7] 🟠 Frontend: no design-system primitives yet (phase-4 goal).**
  - label + input + `invalid-feedback` markup repeated ~15×
  - `isInvalid/errors/hasBackendError/getBackendErrorsSection` duplicated verbatim in 4 components (`edit-type.ts:291-327`, `edit-attribute-dialog.ts:132-171`, `edit-relation-offcanvas.ts:87-175`, `dynamic-input.ts:41-56`)
  - CRUD table, row actions, loading/error blocks duplicated between `schema.component` and `data-overview.component`
  - hard-coded colours bypass the theme (`icon-select.scss:5`, `type-select.scss:5`, `schema.component.scss:19,22`, `theme/_customization.scss:26-64`; D3 colours as TS literals in `network-graph.config.ts:32-47`)

  → Seed `common/ui` with `<chronos-form-field>`, `<chronos-data-table>`, `<chronos-row-actions>`, `<chronos-paginator>`, `<chronos-resource-state>`; semantic CSS custom properties in `theme/_tokens.scss` (graph reads them via `getComputedStyle`); split `theme/` into tokens / bootstrap-overrides / utilities; a shared `BaseValueAccessor<T>` for the 4 custom form controls.
- **[X-8] 🟢 Config naming inconsistencies.** `APP_GDP_HOST` vs `APP_GDB_*` (HDS `application.properties:7`, compose `:34`); SDS client URL assembled from host/port/basepath with hard-coded `http://` (`SchemaClient.java:24`); stale `app.image.path` copied into commons/HDS test properties; invalid `spring.logging.level.*` / `spring.jpa.hibernate.show-sql` keys in `application-debug.properties` of SDS/ui/wiki; ui-service contains a copy-pasted, unused wiki `CachingConfig`.
- **[X-9] 🟢 Obsolete modules/files.** `chronos-frontend/pom.xml` (WebJar leftover, still in root `<modules>`); `docker-compose.dev-cluster.yaml` (monolith image, Postgres volume on Neo4j); `backup/`; root-owned empty `data/img`.

---

## 4. Correctness & data integrity

### HDS
- **[C-1] 🔴 `@Transactional` is ineffective.** `hds/service/DataService.java:23` vs `persistence/CypherClient.java:36,51` — raw `driver.session()` auto-commit sessions; find → validate → write are separate transactions. → `Neo4jClient` (joins Spring TX) or `session.executeWrite/executeRead`, one unit of work per use case.
- **[C-2] 🔴 Create accepts client `elementId` → duplicate keys.** `DataService.java:78-84`: `IsChangeable` finds the referenced node, `IsUnique` skips it as "self", `CREATE` makes a second node with the same key; `update`/`delete` then match by label+key (`CypherService:192-194`) and hit both. → Ignore/reject `elementId` on create; **Neo4j uniqueness constraint on `key`** as backstop.
- **[C-3] 🔴 Create accepts client `_meta`.** Only `createAuthor` is overwritten; `version`, dates, `lastUpdateAuthor` persisted as sent (`EntryMapper.java:68-83`). → Create-request DTO without `_meta`; `MetaInfo.forCreation(author, clock)`.
- **[C-4] 🔴 Missing mandatory attribute → 500.** `ChronosHistoricalDataServiceExceptionHandler.java:49` `Map.of("value", null)` throws NPE; `IsMandatoryValidationRule` always reports null. → Null-tolerant map + IT.
- **[C-5] 🟠 Filter values are always strings.** `BaseAttributeFilter.java:11`, `CypherDslUtils.java:100-122`: `height=178` never matches numbers; `gt/lt` on DATENOTATION compares lexically (`"-44" < "-45"`, `"9" > "10"`). Directly relevant for EDTF plans. → Schema-type-aware value conversion ([E-2]); store a sortable normalized date (e.g. day index / EDTF lower/upper bound) next to the notation string.
- **[C-6] 🟠 No optimistic locking.** `_meta.version` incremented but never compared (`DataService.java:86-94`). → Conditional update on version, 409 on mismatch.
- **[C-7] 🟠 Update semantics undefined.** PUT only SETs present attributes (`CypherService.java:199-210`), validation runs on the partial body, not the merged state. → Define PUT as replace (REMOVE missing) or explicit merge/PATCH; validate the resulting entry.
- **[C-8] 🟠 Multi-label entries are non-deterministic.** Type = `labels.stream().findFirst()` on a `HashSet` (`ValidationService.java:66`, `EntryMapper.java:69`, `CypherService.java:191`, `DataService.java:99`). → Separate `type` from `labels` in the meta-model, or reject >1 label.
- **[C-9] 🟠 Smaller HDS defects.**
  - `Entry.equals` uses `elementId`+`key` but `hashCode` hashes the whole attribute map (`Entry.java:15-24`), which breaks HashSet dedupe in mesh
  - `asInt()`/`asFloat()` on 64-bit Neo4j numbers, and unknown types silently become `null` (`EntryMapper.java:94-115`)
  - `EntryFilterExtractor` drops repeated params and ignores inherited fields (`:26-80`)
  - statistics unordered and use first label only
  - unique check compares string values only and has a check-then-act race
  - `SecurityService` NPE without authentication
  - timestamps are zone-less `LocalDateTime.now().toString()` with no injectable `Clock`

### Commons / SDS / wiki / ui
- **[C-13] 🔴 Update endpoint doesn't bind path to body.** `AdminSchemaController.java:35-49` only checks `{key}` exists; body id null/different → create/duplicate or overwrite another type; response built from the request body, not persisted state.
- **[C-14] 🟠 Inverted predicate.** `sds/business/DefaultTypeAttributesRule.java:39-41` `isDefaultAttribute` returns true when the key is *not* a default (`noneMatch`); `SchemaService.java:72` relies on it. Posting an attribute named like a default is silently dropped instead of rejected. → Rename/invert + `ReservedAttributeKeyRule` (400).
- **[C-15] 🟠 JPA mapping issues.**
  - Unidirectional `@OneToMany @JoinColumn` without `orphanRemoval`/cascade (`TypePO.java:51-55`, `RelationPO.java:40-44`), so removed attributes linger with `type_id = NULL`
  - `@Data` on bidirectional entities causes `toString` stack overflow and hashCode over lazy collections inside HashSets
  - `jakarta.transaction.Transactional` instead of Spring's, no `readOnly`
  - cache eviction may run before commit
  - `save(RelationPO)` not evicting
  - self-invocation bypasses proxies (`SchemaService.java:25-26,60-102`)
- **[C-16] 🟠 Cache holds attached, mutable, lazy JPA entities** (`SchemaService.java:46-54`), which needs `enable_lazy_load_no_trans` in tests. → Cache immutable DTOs/`SchemaResponse`.
- **[C-17] 🟠 `allowedValues` storage.** `StringSetConverter.java:12-21` joins with unescaped `;`, 64 elements into `VARCHAR(1024)`, empty set becomes null. → `@ElementCollection` or JSON(B).
- **[C-18] 🟠 Liquibase changelog gaps.** New `is_unique`/`is_changeable` columns are nullable with no default (Java defaults false/true); no unique constraint on `(type_id, technical_key)` or relation key per source; mixed XSD versions.
- **[C-19] 🟠 Commons error handler bugs.**
  - `RestExceptionHandler.java:81-82` handles `NoResourceFoundException`, but its parameter type is `NotFoundException`, so the handler can't be invoked and the result is 500 instead of 404
  - `Size` branch hard-casts args (`:131-133`)
  - `ChronosJwtAuthConverter.java:72,89-95` unchecked casts cause an NPE when `roles` is absent, and a blank `principleAttribute` is accepted
- **[C-20] 🟠 Wiki: `catch (NullPointerException)` as control flow** (`WikipediaService.java:81-95`), which hides real bugs. ui-service `ImagesController.java:22-30`: `assert files != null` is a no-op in prod, giving NPE / `nextInt(0)` → 500, and reads the whole file per request.

### Frontend
- **[C-10] 🔴 Edits to type-specific attributes are discarded on save.** `fe/modules/admin/views/data/edit-entry/edit-entry.component.ts:182-196`: `save()` → `updateType()` copies back only keys contained in `defaultAttributes`.
- **[C-11] 🔴 Cancelling a delete still leaves the page.** `admin-data.service.ts:34-53`, `admin-schema.service.ts:71-91`: dismissal handler returns `undefined`, so the promise resolves and callers `back()` (`edit-entry.component.ts:175-179`, `edit-type.component.ts:239-241`). → Typed result `'deleted' | 'cancelled'`.
- **[C-12] 🔴 Route-scoped `HTTP_INTERCEPTORS` never run.** `fe/modules/admin/admin.routes.ts:19-31` registers `AdminErrorInterceptor`/`AuthInterceptor` in route providers; root `HttpClient` reads interceptors from the root injector only. Tokens are attached by the library's default interceptor anyway; admin error toasts never fire. → Delete `AuthInterceptor`; functional `HttpInterceptorFn` via `provideHttpClient(withInterceptors([...]))`.
- **[C-21] 🟠 Signal/form state bugs.**
  - `edit-entry` rebuilds its `FormGroup` inside a `computed` on every `entry.update()`, losing dirty/touched state (`:121-124`)
  - `edit-type` reads the route signal once (`:65`), so navigation doesn't reload
  - in-place mutation of resource values (`type.attributes[i] = …`, `splice`, `push`) never notifies signals and only works under zone CD (`edit-type.component.ts:145-150,186-191,260,279`, `edit-relation-offcanvas.ts:129-134,152`, `edit-attribute-dialog.ts:151,160-161`)
  - untyped forms, `toAO(formData: any)`
- **[C-22] 🟠 UI behaviour defects.**
  - `setDisabledState` is empty in `date-input`/`wiki-article-input` (`:75-77`, `:86-88`), so non-changeable attributes stay editable
  - `valueRange.split('-')` breaks negative years (`entry-attribute-form.service.ts:36-43`)
  - specific-attribute block lacks `[disabled]`/`[backendErrors]`, so backend errors are never shown (`edit-entry.component.html:120-161`)
  - `userName$() | async` re-subscribes on every CD cycle (`navbar.component.html:55`)
  - each `allTypes()` call creates a new `rxResource`, refetching `/api/schema` per `TypeSelect`, and a second uninvalidated "cache" exists (`admin-schema.service.ts:28-41`); use a single `SchemaStore` with `reload()`
  - arrowhead markers never render (`network-graph.component.ts`: `url(#arrow)` vs `arrow-<type>`)
  - `start:mock` script references a non-existent serve configuration

---

## 5. Security

- **[S-1] 🔴 Token-minting capability ships in production artifacts.** `chronos-commons/src/main/resources/dev-jwt-private.key`, `commons/security/TestSecurityConfig.java:21-44`, `TestJwtGenerator.java:23-54` live in **main** sources → in every jar/image; activating `test`/`test-security` installs a `JwtDecoder` trusting the committed key; `TestJwtGenerator` logs user/admin JWTs valid ~100 years at INFO; `.gitguardian.yaml` suppresses the key. → Move to a `chronos-test-support` module / commons `test-jar` (`<scope>test</scope>`), generate key pairs per test run, remove token logging, short expiry.
- **[S-2] 🔴 Security can be switched off by profile in production jars.** `{hds,sds,wiki}/config/security/NoSecurityConfig.java` + `application-no-security.properties`: `anyRequest().anonymous()/permitAll()` opens admin writes; only a `logger.warn`. SDS `in-memory-persistence` additionally enables the **H2 console** while H2 + devtools are runtime deps (`sds pom.xml:65-69,92-97`). → Move to test sources or add a fail-fast startup guard; H2/devtools out of the prod jar.
- **[S-3] 🟠 Unencoded user input in outbound URLs.** Wiki `client/WikipediaApiClient.java:30-39,56-61,79-88` concatenates `title`/`qid` (only `startsWith("Q")` checked) → parameter injection into Wikipedia API calls, `{}` breaks URI-template parsing → 500. HDS `SchemaClient` concatenates the client-supplied label. FE `api-unique-validator` ([L-10]). → `UriComponentsBuilder`/URI variables, strict `^Q\d+$`.
- **[S-4] 🟠 Unbounded / unvalidated anonymous queries.**
  - HDS `Pagination.pageSize` has no `@Max`, and `(page-1)*pageSize` can overflow into a negative SKIP
  - anonymous `POST /mesh` has no LIMIT (empty query returns the whole graph; `types:["*"]`)
  - filter/sort property names are not checked against the schema, so internal `_`-prefixed properties are queryable (`EntryFilterExtractor.java:58`, `CypherDslUtils.java:82,133`)
  - wiki caches arbitrary `Q…` ids in an unbounded map

  → Upper bounds, hard LIMIT + query timeout, schema-validated property names, bounded caches ([P-1]). Cypher injection itself was *not* found — Cypher-DSL escapes identifiers/literals — but statements inline literals instead of parameters (`CypherClient.java:28-49`) and debug-log full statements with user data; switch to `Cypher.parameter(...)`.
- **[S-5] 🟠 Frontend auth flow weaknesses.** `fe/general/security/auth.service.ts:22-36`, `app-routing.module.ts:14-16`:
  - discovery isn't awaited before the guard may call `initCodeFlow()`
  - the guard checks for a token, not the admin role (the menu shows "Admin" unconditionally)
  - post-login redirect loses the target route
  - `offline_access` puts the refresh token in `sessionStorage`
  - `strictDiscoveryDocumentValidation: false`

  → Initialise in `provideAppInitializer`, async guard with role check + `UrlTree`, reconsider `offline_access`.
- **[S-6] 🟠 Edge hardening missing.**
  - `chronos-gateway/traefik.yml:10-12` has the dashboard with `insecure: true`
  - HTTP only: no TLS entrypoint, no security headers or rate limiting (`dynamic.yml`); `/api` catch-all routes to HDS (`dynamic.yml:27-31`)
  - `chronos-frontend/nginx.conf` has no CSP / `X-Content-Type-Options` / `Referrer-Policy` / `frame-ancestors` / `server_tokens off`
  - Swagger UI + `/v3/api-docs` enabled in prod on every service

  → Prod gateway config (TLS, headers and rate-limit middleware, explicit `/api/data` prefix), nginx security headers + cache headers, `springdoc.*.enabled` from env.
- **[S-7] 🟢 Information disclosure & secrets hygiene.** `RestExceptionHandler.java:75` echoes `HttpMessageNotReadableException` messages (Jackson/class internals); plain-text DB passwords in `docker-compose.yaml`, SDS `pom.xml:143-145`, HDS `application-local-persistence.properties`; all DB/service ports published on `0.0.0.0` in compose; containers run as root ([D-1]); ui-service disables frame options without rationale.

---

## 6. Maintainability & code hygiene

- **[M-1] 🟢 Dead code / leftovers.**
  - HDS `ChronosDateSpecValidator` is unused, and its pattern `\d{1,4}` contradicts `IsCorrectType`'s `\d{1,6}` (keep one DATENOTATION/EDTF validator)
  - `org.reflections` dependency unused (HDS pom `:39-43`)
  - SDS `RelationPORepository.findByKey`, empty `CachingConfig.customize`, unused loggers
  - wiki `BaseIntegrationTest.getEntity` calls `/api/schema`
  - FE: `console.log`s, unused `AdminWikiService`, `onlyDefaults` mappers, commented-out blocks, placeholder footer links, `['dummy']` route, no `**` not-found route, outdated `network-graph/README.md`
  - unused npm deps `ngx-color-picker`, `@angular/cdk`, `@angular/animations`; misaligned FontAwesome versions
- **[M-2] 🟢 Stale TODOs.** `ValidationService.java:31-36` still lists `isChangeable` as missing (it exists — `CLAUDE.md` repeats this); GH-23 TODO in `CypherService.java:203`.
- **[M-3] 🟢 Logging.** Validation logs at INFO per call/attribute (`ValidationService.java:48`, `IsUniqueValidationRule.java:30,35`); string-concatenated log messages in wiki; exceptions without messages (`NotFoundException()`, `InvalidParameterException()`); typo `StringToSupportedLanguagConverter`, `notifcation.model.ts`.
- **[M-4] 🟢 DI style.** Field `@Autowired` is the documented convention but prevents plain unit tests ([T-3]); `SchemaClient` mixes constructor and field `@Value`. → Consider constructor injection (`@RequiredArgsConstructor`) + `@ConfigurationProperties` as the convention for new code, and update `CLAUDE.md` accordingly.
- **[M-5] 🟢 Accessibility & i18n (FE).**
  - icon-only buttons without `aria-label`
  - clickable `<td>`s are not keyboard-accessible
  - "Toggle navigation" label on the logout button
  - `@angular/localize` is loaded but there are zero `i18n` usages

  → Either adopt i18n markers now (backend already sends message keys) or drop the polyfill; template a11y lint rules.

---

## 7. Testing

- **[T-1] 🔴 SDS tests don't exercise the production persistence setup.** `chronos-schema-definition-service/src/test/resources/application.properties:6-8`: H2, `liquibase.enabled=false`, `ddl-auto=update`, `enable_lazy_load_no_trans=true`. → Postgres Testcontainer (`@ServiceConnection`) with Liquibase on and `ddl-auto=validate`.
- **[T-2] 🟠 HDS test infrastructure is slow and cache-hostile.** 11 IT classes each start their own unpinned `neo4j:5` container; varying `@MockitoBean`/`@ActiveProfiles` break Spring context caching; `TestDataManager` (in *main* sources) polls and re-imports per test and splits scripts on `;`. → Singleton container in `BaseIntegrationTest`, pinned tag, mocks declared once, seeding in `src/test`, `DETACH DELETE`.
- **[T-3] 🟠 Inverted test pyramid.** HDS's only "unit" test boots `@SpringBootTest` (`ValidationServiceTest.java:31-33`); no plain tests for Cypher rendering, `EntryFilterExtractor`, `EntryMapper`, operators, individual rules; SDS lacks unit tests for `UniqueValidator`, `DefaultTypeAttributesRule`, `ModelMapper`; commons has no tests at all; ui-service has no `src/test`; wiki has no client tests (`MockRestServiceServer`/WireMock).
- **[T-4] 🔴 Frontend tests are broken and not run.** CI (`.github/workflows/build.yml:43-44`) runs only `npm ci && npm run build`; the 19 specs are CLI boilerplate that would fail (`app.component.spec.ts` expects scaffold text, missing providers), `fdescribe` in `time-util.spec.ts`; Karma is deprecated. → Migrate to `@angular/build:unit-test` (Vitest), delete boilerplate, test mappers/form services/attribute registry, add to CI.
- **[T-5] 🟠 Missing negative/edge-case coverage** — each documents a fix above:
  - tampered `_meta`/`elementId` on create
  - mandatory attribute over HTTP
  - authenticated user on public endpoints (403 from `anonymous()`)
  - SDS unavailable / unknown type
  - numeric and negative-year range filters
  - multiple relation filters
  - concurrent updates
  - `pageSize` bound
  - PUT with mismatched path/body key
  - foreign attribute id
  - upstream wiki errors / encoding
  - `/v3/api-docs` smoke test
- **[T-6] 🟢 Flaky/misplaced tests.** Random-sort test asserts two random pages differ; label-filter tests live in the sorting IT; statistics test depends on result order; `CachingIntegrationTest.java:206` comment contradicts code.
- **[T-7] 🟢 No coverage gates.** JaCoCo reports only, no `check` thresholds or aggregate report; commons without JaCoCo.

---

## 8. Build & dependencies

- **[B-1] 🔴 Platform currency *(verify)*.** Spring Boot 3.5.7 (`pom.xml:15`) — 3.5.x OSS support is believed to have ended mid-2026; Java 17 is two LTS behind (21, 25). → Plan Boot 4.0 + Java 21/25; set `java.version` once in root.
- **[B-2] 🟠 springdoc 2.2.0 *(verify)*** targets Boot 3.1 and is likely incompatible with Spring Framework 6.2 (`/v3/api-docs` failures); declared twice (root `:35`, HDS `:49`). → 2.8.x / 3.x, root-managed only, smoke IT.
- **[B-3] 🟠 HDS doesn't inherit the root POM** (`chronos-historical-data-service/pom.xml:5-13`, hard-coded commons `0.0.1-SNAPSHOT`, name `chronos-backend`). → Parent `net.fvogel:chronos`, `${project.version}`.
- **[B-4] 🟠 POM duplication.** JaCoCo executions, lombok exclusion, `inContainer` profile, `java.version`/compiler props copy-pasted in 4 service POMs; starters re-declared although commons brings them. → Root `pluginManagement`/`properties`/`profiles`.
- **[B-5] 🟠 Testcontainers version mix.** `testcontainers-neo4j:2.0.2` on a Boot-managed 1.21.3 core (HDS `pom.xml:96-111`). → Consistent BOM.
- **[B-6] 🟢 Reproducibility.** No Maven wrapper, no enforcer (Java/Maven version, `dependencyConvergence`), no `project.build.outputTimestamp`; Liquibase plugin DB coordinates don't match compose (`sds pom.xml:141-155`); SDS README references a non-existent `local-persistence` properties file.

---

## 9. Delivery: containers & CI/CD

- **[D-1] 🟠 Service Dockerfiles** (4 identical copies): root user, floating `17-jre-alpine`, no `HEALTHCHECK`, no JVM container flags (`MaxRAMPercentage`, `ExitOnOutOfMemoryError`), no layered-jar extraction, `CMD` instead of `ENTRYPOINT`. → One parameterised Dockerfile (`ARG SERVICE`), non-root, digest-pinned base.
- **[D-2] 🟠 Frontend/gateway images.**
  - frontend: `nginx:latest`, root, and a fragile `COPY dist/chronos-frontend/*` glob over Angular's `browser/` output
  - gateway: Dockerfile copies no config (useless without mounts), is not in the CI matrix, is pinned to old `v3.0`, and routes `ui` to the Angular dev server port 4200, so the config can't serve the prod nginx image
- **[D-3] 🟠 docker-compose.**
  - no healthchecks / `condition: service_healthy` (HDS/SDS race the DBs)
  - adminer mapping `7000:7000` is unreachable (container listens on 8080)
  - `~/.m2` is mounted to `/root/.m2`, so files written are root-owned (hence `sudo` in `cleanup.sh`)
- **[D-4] 🟠 CI (`.github/workflows/build.yml`).**
  - no `permissions:` block, and actions are tag-pinned instead of SHA-pinned
  - no Dependabot / CodeQL / image scanning / `npm audit`
  - Node 20 in CI vs Node 24 elsewhere
  - `mvn clean test` + `mvn clean package -DskipTests` compiles twice (use `mvn -B verify`)
  - no test/coverage report upload
  - images only on release (no `edge` from main), no buildx cache, SBOM or provenance; unused `IMAGE_NAME`; hard-coded Docker Hub user

---

## 10. Documentation

- **[DOC-1] 🟠 `CLAUDE.md` references.**
  - `doc/plan/phase 4 - first release.md` is cited as authoritative but **doesn't exist** (only phases 0–3)
  - the Liquibase procedure is in `chronos-schema-definition-service/README.md`, not `chronos-commons/README.md`
  - the "query models mirrored 1:1" claim is false ([X-1])
  - `isChangeable` is listed as missing but exists
- **[DOC-2] 🟠 Stale docs.**
  - `doc/build.md` describes a single image, a `docker-publish.yml` workflow, signed images and a `master` branch, none of which exist
  - `doc/deployment.md` documents `DB_*` variables (code uses `APP_DB_*`), omits all HDS variables and has a wrong app-config path
  - `doc/development.md` is from the monolith era (Node 16, WebJar)
  - `doc/issues.md` covers H2 `LocalDate`
  - `doc/structure.md` and `release-notes.md` are empty
  - the SDS README references a non-existent `docker-compose.dev.yaml`/`chronos-sds`
  - `proxy.conf.json` targets `chronos-sds`/`chronos-hds:8080`

  → Per-service env-var tables generated from `application.properties`; delete or fill stubs.
- **[DOC-3] 🟢 Architecture decisions are scattered.** Decisions currently live in `CLAUDE.md`, phase notes and READMEs. → Introduce lightweight ADRs (`doc/adr/NNNN-*.md`) for e.g. additive-only evolution enforcement, attribute-type SPI, contract module/OpenAPI codegen, per-service roles, EDTF storage normalisation.

---

## 11. Not yet addressed aspects (high level)

- **Observability.**
  - no `spring-boot-starter-actuator`, so there are no health/liveness/readiness, metrics or build info endpoints
  - plain-text logs with no correlation/trace ID across gateway → HDS → SDS
  - no request logging

  → Actuator on a separate management port (not routed via the gateway), Boot structured logging (ECS/JSON), Micrometer tracing (OTel) with Traefik tracing, Prometheus metrics.
- **Resilience [R-1].** `RestClient.create()` without timeouts in HDS `SchemaClient.java:19-21` and wiki `WikipediaApiClient.java:16-18`; no retries or circuit breakers; SDS outage blocks all HDS writes; upstream errors surface as 500 instead of 502/503; Wikimedia User-Agent policy not met (no contact). → `RestClient.Builder` with timeouts, Resilience4j, short-TTL schema cache in HDS, graceful shutdown.
- **Scalability [P-1].**
  - in-process, unbounded, TTL-less `ConcurrentMapCacheManager` in SDS/wiki; SDS eviction is node-local, so replicas would serve stale schemas
  - ui-service reads images from a local filesystem, so every replica needs a volume
  - SDS `GET /api/schema` has N+1 lazy-loading cascades
  - Neo4j statements aren't parameterised, so there is no plan caching
  - no Neo4j indexes/constraints managed in code (e.g. on `key`)

  → Caffeine with bounds now, distributed eviction/TTL later, `@EntityGraph`, schema migrations for Neo4j constraints (e.g. neo4j-migrations).
- **API lifecycle.** No API versioning (`/api/data`, `/api/schema`) despite the phase-4 "versioned additive-only contracts" direction; no contract tests between HDS and SDS. → Decide on `/api/v1` or media-type versioning before first release; consumer-driven contract tests or a shared generated contract.
- **Data operations.** No backup/restore procedure for Neo4j and Postgres, no retention; no migration tooling for Neo4j (only SDS has Liquibase).
- **Auditability.** `_meta` authorship exists but no change history. The planned review service will cover part of this; consider domain events now (entry/type created/updated) as a cheap seam for the future review service, evidence nodes and cache invalidation.
- **Frontend runtime.** Zone-based change detection with mutable state blocks OnPush/zoneless; no bundle budgets matched to reality; no error monitoring (e.g. Sentry-style) or web-vitals.
- **Supply chain.** No dependency update automation, no SBOM, no license scanning.
