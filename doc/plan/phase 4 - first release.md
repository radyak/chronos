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
<!--

A shareable, forkable "social layer" around saved analysis pipelines — the most ambitious and highest-UX-risk part of the project.

- **Typed dataflow blocks**, each with a fixed input/output shape (like Node-RED/Blueprints) — the UI only offers valid connections, preventing broken pipelines by construction.
- **v1 query stage should stay shallow**, form-like filters (entity type, one-hop relation filter, date-range/attribute predicate) rather than full graph pattern matching — the Roman Emperors example doesn't need more than this. Multi-hop path queries deferred to a later block version.
- **Transform stage** ≈ pivot-table mental model (group/aggregate) — reuse familiar UX (Airtable/Tableau-like), don't invent new interaction patterns.
- **Display stage**: constrain chart type choices to whatever the transform's output shape supports; consider a declarative grammar (Vega-Lite-style) to avoid bespoke glue per chart type.
- **Block contracts are versioned the same way as schema** — additive-only; breaking changes get a new block version so old pipelines don't silently break.
- **Snapshot/pin data versions behind shared pipelines** — since facts are never fixed, a citation needs a reproducible state, not a live-changing query result.
- **Sequencing:** don't launch with an open builder. Launch with a handful of polished, hand-built example pipelines demonstrating the payoff → let users fork/tweak parameters → only build the full open builder once real usage shows what people actually want to build.
-->


## 9. Priority of features
<!--
'The direction and priority which user group to focus on and which functionality to provide in the first version is not clear.
-->


---

# Scope of First Release
<!--
- **First vertical slice:** parallel regional timelines (not all four use cases at once).
- **Fixed starter type set** (Person, Territory/Polity, Dynasty, Denomination, Event, Source) rather than building the schema-admin UI before there's real content to justify it.
- **Deliberately over-curate 3–4 regions deeply** (even though region selection is flexible from day one) so demo paths reliably show rich data rather than empty timelines.
- **Statement-level sourcing by default**, sub-statement promotion only where disputed.
-->

---

# Open Questions
<!--
- Should the review queue (for facts and schema proposals alike) be a public talk-page-style queue or a private admin inbox at this stage?
- Timeline display semantics: one row per entity (Gantt-style reign bars) vs. plotting ranges as points on a shared regional axis?
- How schema-admin authority evolves/delegates as the contributor base grows.
-->