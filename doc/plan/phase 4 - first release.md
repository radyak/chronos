# Phase 4 - Preparation for First Release
With the Consultation of AI ([phase 3](phase%203%20-%20interview%20with%20ai.md)), some open issues from the MVP ([phase 2](phase%202%20-%20mvp.md)) were addressed, but also new questions arose.


## 1. Increasing Complexity
Now that the dimensions of domain logic and code base has passed a certain size, measures should be taken to handle this.

> 💡 ***Decision:***
> - Use AI, especially for broad and comprehensive, but straight-forward changes
> - Re-align code (also with AI) to comply with Architecture and reduce unnecessary couplings


## 2. Design "blots" 
Quick (& dirty) fixes, shortcuts and resulting workarounds can be dangerous, especially if they can take effect from early stages. Thus the design should be adjusted and consolidated again.

> 💡 ***Decision:***
> - List self-indicated defects
> - Define a design & architecture ruleset for AI
> - Let AI analyze and detect implementation flaws and suggestions to fix them
> - Adjust code (manually or with AI) to 


## 3. Inconsistent UI
Chronos doesn't even have a design system or a UI Kit. Thus, even rather simple UI section were built inconsistently and look odd.

> 💡 ***Decision:***
> - Use AI to
>   - establish and document a Design System
>   - get a CSS setup/framework/theme suggested
>   - establish a UI Kit
> - Fix flaws in code (or get AI suggestions)


## 4. Specific Date Format
The often difficult availability of data renders the possibility to express fuzzy dates a requirement. Variants such as ranges, multiple distinct possible values or simply placeholders for entirely unknown date components should be expressible with a respective date format.

> 💡 ***Decision:***
> Look into EDTF (Extended Date/Time Format)** rather than inventing notation — it already handles unspecified digits, "one of several possible dates," ranges, and uncertainty qualifiers.
> AI suggested to "add a **calendar system field** (Gregorian/Julian/Hijri/etc.) alongside precision, since Julian–Gregorian mismatches and non-Western calendars are unavoidable at global scope." - however, since this is a mere data representation aspect, it must be considered to follow a common date standard in the data base and respect conversion only during editing and querying. 


## 5. Foundation for data scientificity
The representation of sources, evidences and controversies - in short: *verifiability* - is **very important** to the overall functional foundation. Thus, AI suggested "Reification" - i.e.: also Relations would be nodes, so that verifyability data can also be linked to relations. 
**However, the verifyability data is per se not part of the domain model** and rather an additional, orthogonal aspect (like version and approval information, see *point 6.*). While a graph model (nodes + relation) would nearly perfectly reflect the domain's requirements, the suggested Reification would squeeze a second, different dimension into the otherwise consistent modeling - if not even defeat the actual purpose of a graph database / model at all, making the effective model unmaintainable.
Plus, also other data, such as maps, time development etc. which could be added later, could also be attributed with verifyability data - so this aspect has to live in its own realm anyway.

> 💡 ***Decision:***
> - The related data will be a sub-set of data nodes (e.g. "_evidence" or similar)
> - Fields could be
>   - *status* (e.g. "secured", "debated" etc.)
>   - *sources* (array of literature references)
>   - *verification* (0="impossible" - 1="historically verified")
> - *Sources* would live and be maintained in another service
> - This also allows queries by *source* of *verification* factor
> - Evidence data *per attribute* would be overkill, so *only per relation & entry*


## 6. Review & approval process
The claim for data scientificity, which already implies sources and evidence state from point 5, will require some kind of peer reviews, similarly as it is done for scientific papers, including a respective process. This should ensure data quality and reliability, but it not slow down contribution at the same time.

> 💡 ***Decision:***
> - The first release will not include such processes - *AI Recommendation*: launch curated/seeded, open editing later** — a wiki with little content and no visitors doesn't attract good contributors; bootstrap like Wikidata did, via bulk import + curation first
> - Later on, there would be an extension, based on a respective review service
>   - The service should cover all "reviewable" entities of the overall system (i.e. schema, entries, labels) and contain revision history → e.g. document-based database like MongoDB
>   - UI must be extended respectively; Migration will require: creation endpoints must be protected and callable only from review service; UI parts will be reusable
>   - Universally unifrom process (proposed → in review → approved/published)
>   - Versions per entity
>   - *AI recommendation*: launch curated/seeded, open editing later** — a wiki with little content and no visitors doesn't attract good contributors; bootstrap like Wikidata did, via bulk import + curation first



## 7. Data & Schema Governance
Other than standards like FHIR (health domain), Chronos' schema will have to react often and quickly, especially in the beginning, when data & modelling scale up fast. Thus, it must be considered, wether a hard-coded or a rather dynamic schema definition would be preferrable.

> 💡 ***Decision:***
> We will mostly follow the *AI recommendations* here:
> - **Meta-model** (i.e. how a schema is defined) **is fixed in code**.
> - **Relation/entity *types*** (i.e. schema) **are data, not code** — a governed type registry, extensible without deployment.
> - Type changes go through the **same review workflow** as data facts (extension point)
> - **Additive-only changes** by default
>   - New optional attributes (soft CREATE)
>   - Changing (UPDATE)/removing (DELETE) and even new mandatory attributes (hard CREATE) would either need a full migration - or mean potential inconsistencies (extension point)
> - Start with one **single trusted schema admin**


## 8. Query-Transform-Display Pipelines
The structured data alone will only suffice a few use cases. What is needed, are user-friendly ways to query the data and display it in different ways - both connected with transformations of the data. Thus, three stages are relevant for data intelligence, which form some kind of pipeline: query, transformation, display. All of such data-flow blocks should be bound to contracts, which also express compatibility among them and allow to only build functioning pipelines from the beginning. 

> 💡 ***Decision:***
> We will basically follow AI's recommendation, with some tweaks to its suggestions:
> - Query stage: should stay shallow
> - Transform stage: ≈ pivot-table mental model (group/aggregate) — reuse familiar UX (Airtable/Tableau-like), don't invent new interaction patterns.
> - Display stage: constrain chart type choices to whatever the transform's output shape supports; consider a declarative grammar (Vega-Lite-style) to avoid glue per chart type.
> Important:
> - For the first release, we won't launch with an open builder. Launch with a handful of polished, hand-built example pipelines demonstrating the payoff. Users would be able to fork/tweak parameters. A full open builder will follow once real usage shows what people actually want to build. 
> - However, all stubs should be designed with the extensions in mind already! I.e. interfaces for data-blocks, keep it possible to develop full-fledged queries e.g. with multi-hops or Vega-lite-like data interfaces
> All parts should be hard-coded, but also in a way so that a service could replace this functionality 
> In later stages, this infrastructure could be extended to a "social layer" around saved analysis blocks or whole pipelines — AI considered this "the most ambitious and highest-UX-risk part of the project". In the end, it should grow and develop naturally, along the actual user needs and use cases. Then, the blocks should also introduce versioning and snapshots/pinning of data & pipelines/block versions.


## 9. Priority of features
Which user group to focus on and which functionality to provide in the first version will have an impact not only on the release date but also on the future direction of development.

> 💡 ***Decision:***
> The focus should clearly be on the public user's side, i.e. discovering data and using QTD (query-transform-display) pipelines
> - This will be the outward-facing part that represents the prototype and should gather feedback as early as possible
> - Admin aspects will only be relevant to one user in the beginning, and this maybe even only in a reduced scope, since lots of data could be sourced from WikiData
> Thus, focus should be: Some QTD blocks and pipelines (hard-coded data), data sourcing (and attribution/licensing), data scientificity (evidence sources, versioning, debate status, date format) and maybe user auth & storage (e.g. to store pipelines)



---

# Scope of First Release

## Management Summary

The first release is a **not a fully-fleged platform launch** but the **go-live of the very first foundation components** - with the focus on being a **public showcase** at this stage. It is a narrow but deep vertical slice that lets an anonymous visitor explore curated historical data and see what a schema-governed history *graph* can do that a prose wiki cannot. Target audience are first users, potential supporters and investors — i.e. the outward-facing, public side of the product (point 9), not the curation side.

It has to demonstrate three things, and nothing more, yet:

1. **Structured, typed, related data** — entries and relations against a governed type registry, browsable and queryable.
2. **Scientificity** — evidence, sources and verification state attached to entries and relations, and fuzzy dates expressed in a real standard (EDTF) instead of a homegrown notation.
3. **Payoff** — a handful of polished, hand-built query-transform-display (QTD) pipelines that turn that data into infographics, with parameters the visitor may fork and tweak.

Everything else is deliberately deferred. Curation stays with *one trusted admin** and is bootstrapped by sourcing data (Wikidata/Wikipedia) rather than by attracting contributors. There is **no review & approval process**, **no open pipeline builder**, **no multi-admin governance** in this release — but each of these is kept as an explicit extension point so the first release does not have to be unbuilt to add them.

Two cross-cutting preconditions come before feature work, because they are what phase 4 diagnosed as the real risk (points 1–3): an **architecture ruleset plus a clean-up pass** on the existing code, and a **design system / UI kit**, since without one every screen of the showcase would repeat the inconsistency that made the MVP look improvised.

### Decisions and what they mean for this release

| § | Decision | Consequence for the first release |
| --- | --- | --- |
| 1 | AI for broad, straight-forward changes; re-align code to the architecture | Stage 1 — ruleset first, then the clean-up pass |
| 2 | List defects, define a design & architecture ruleset for AI, detect and fix flaws | Stage 1 — prerequisite for all following stages |
| 3 | Establish and document a design system, CSS theme and UI kit | Stage 2 — prerequisite for every public screen |
| 4 | EDTF instead of own notation; one storage standard; calendars are an edit/query concern | Stage 3 — EDTF in; calendar conversion deferred |
| 5 | Verifiability as orthogonal `_evidence` data nodes per entry & relation; no reification | Stage 4 — in; a dedicated *sources* service deferred |
| 6 | No review & approval process in the first release; seed & curate first | Out of scope; creation endpoints stay isolated enough to lock down later |
| 7 | Meta-model in code, types as data, additive-only, single schema admin | Stage 5 — enforced; non-additive changes are an extension point |
| 8 | Shallow query, pivot-like transform, shape-constrained display; no open builder | Stages 6–7 — typed contracts + hand-built pipelines |
| 9 | Focus on the public user: discovery and QTD; admin minimal; source from Wikidata | Sets the order of the whole plan; Stages 6–8 are the visible release |

### Not in the first release

Review & approval workflow and revision history (point 6) · a separate sources service (point 5) · schema updates,
deletions and new mandatory attributes (point 7) · calendar-system conversion (point 4) · open QTD builder, pipeline
versioning/snapshots and the social layer (point 8) · multi-admin governance and delegation (point 7).

## Plan

### Stage 1 — Engineering baseline (points 1, 2)
- Write the **design & architecture ruleset** for AI into the repo: layer separation (`rest` → `service` →
  `persistence`/`client`), mapping at the boundary, extension points instead of branching.
- List the **self-indicated defects** (DTO/domain/AO mix in HDS, stale docs, dead JaCoCo exclusion, proxy/container name mismatch) in `doc/issues.md`.
- Run the AI analysis against the ruleset and fix the flagged layer violations and couplings.
- *Done when:* ruleset committed, defect list current, boundaries mapped, build and tests green.

### Stage 2 — Design system & UI kit (point 3)
- Document the **design system** (colour, type, spacing, states) on top of the existing theme variables and settle the CSS framework/theme setup.
- Build the **UI kit** components the public slice needs and migrate the existing views onto them.
- *Done when:* design system documented under `doc/`, kit components live in `common/`, existing views use them, no new ad-hoc CSS.

### Stage 3 — EDTF dates (point 4)
- Replace the narrow `DATENOTATION` subset with an **EDTF-based attribute type**: parsing/validation as a shared concern,
  ordering and range semantics for queries and sorting, support in the date input component.
- Storage stays on the single standard; conversion for other calendar systems is deferred.
- *Done when:* EDTF values validate, persist, sort, filter and render; existing date values migrated.
- *Before data sourcing, because imported dates must land in the final notation.*

### Stage 4 — Evidence sub-graph (point 5)
- Introduce `_evidence` data nodes carrying *status*, *sources* and *verification*, attached **per entry and per relation** — not per attribute, and without reifying relations.
- Return evidence with entry and mesh reads, allow filtering by status/verification, surface it in the UI.
- *Done when:* evidence can be created, read, filtered and is visible end-to-end.
- *Before data sourcing, because provenance, attribution and licensing of imported data are recorded here.*

### Stage 5 — Schema governance & starter types (point 7)
- Enforce **additive-only** schema evolution in the SDS: new optional attributes allowed; update, removal and new mandatory attributes rejected behind a documented extension point.
- Keep the **single schema-admin** role; seed the fixed starter type set as data, not code.
- *Done when:* the additive rule is enforced and tested, and the starter types are seeded reproducibly.

### Stage 6 — Data sourcing & curation (points 9, 6)
- Build the **sourcing path** from Wikidata/Wikipedia into entries and relations, recording attribution and licensing as
  evidence sources.
- **Over-curate a small number of regions/topics deeply** so every demo path shows rich data instead of empty results.
- *Done when:* the curated corpus loads reproducibly and its licensing is documented.

### Stage 7 — QTD blocks & example pipelines (point 8)
- Define the **typed block contracts** for query → transform → display, versioned and additive-only, with compatibility
  expressed by the block's input/output shape.
- Implement them hard-coded, but behind interfaces, so a dedicated service can take the functionality over later:
  **query** stays shallow (reuse the existing list/mesh filters), **transform** follows the pivot-table model
  (group/aggregate), **display** is constrained to the chart types the transform's output shape supports, ideally via a
  declarative chart grammar rather than glue per chart type.
- Build **a handful of polished example pipelines** over the curated corpus, with forkable/tweakable parameters. No open
  builder.
- *Done when:* the contracts are documented and every example pipeline runs end-to-end over real curated data.

### Stage 8 — Public discovery UI (points 9, 3)
- Browse and query entries, entry detail with evidence and dates, relation mesh via the network graph, and a **gallery of
  the example pipelines** with editable parameters.
- Built entirely from the Stage 2 UI kit.
- *Done when:* an anonymous visitor can go from landing page to browsing to a pipeline result and share it.

### Stage 9 — Optional: accounts & stored pipelines (point 9)
- Only if Stages 1–8 land in time: let authenticated users store their forked pipeline parameters. Read access stays
  anonymous either way.

### Stage 10 — Release
- Deploy the cluster, write release notes and a short demo walkthrough for the showcase audience, and open a feedback
  channel so the first users' reactions steer what follows (open builder, review service, more regions).

---

# Open Questions

## Architecture
- **EDTF scope:** which EDTF level/profile is supported in v1?
- **Evidence attachment:** Should the `_evidence` node be an evidence-carrying property (serialized JSON or commonly prefixed attributes)?

## Domain
- **Verification scale:** define the values between 0 and 1 and how *status* ("secured", "debated") relate to them

## Sourcing
- **Sourcing mechanics:** is the Wikidata/Wikipedia import a one-off script, a dev tool, or a service? Who runs it, and how is re-import/refresh handled without overwriting curation?
- **Licensing:** Wikidata (CC0) and Wikipedia (CC BY-SA) differ — what does the attribution requirement mean for the display of sourced entries and for exported infographics?

## Showcase
- **Starter type set:** which entity and relation types exactly, and which regions/topics get the deep curation — the demo paths depend on this choice being fixed early.
- **Pipeline set:** how many example pipelines, which questions do they answer, and which chart grammar is used?
- **Success criteria:** what would make the showcase a success for first users, supporters and investors, and how is that feedback collected?

## Other
- **Pipeline sharing without accounts:** if Stage 9 is dropped, how is a tweaked pipeline shared — URL-encoded parameters, or not at all?
- **Display semantics for timelines:** one row per entity (Gantt-style bars) or ranges plotted on a shared regional axis?
- **Later, but decided now?** how schema-admin authority evolves and delegates as contributors join, and whether the later review queue is a public talk-page-style queue or a private admin inbox.
