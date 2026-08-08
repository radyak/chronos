# Phase 3 - Sparring with AI
Since it was not clear, 
* what the "market"/need or Unique "Selling" Point could be,
* if the developed technical concept was bullet-proof,
* whether the community and collaboration ideas were feasible and make sense,

the current state (in form of an idea, yet to be started) was discussed with an AI (Claude Sonnet 5).
The results are summarized (by AI) below.


# Historical Research Web App — Design Summary

## Vision
A public, research-grade tool for exploring historical entities and their relationships across time and region — dynasties, parallel regional timelines, currency systems, cause-of-death patterns — built on one generic core model rather than bespoke features per use case.

**Audience:** public/research use (historians, enthusiasts) — this sets a high bar for accuracy, sourcing, and transparency about disputed facts.

---

## Core Data Model

**Everything is a relation, not a bespoke schema per feature:**
`(Subject)-[RELATION_TYPE]->(Object)` with attributes — e.g. `Person -RULED-> Territory {start, end, title}`, `Denomination -VALID_IN-> Territory`, `Person -PART_OF-> Dynasty`.

**Reification for sourcing (Neo4j-specific fix):** Neo4j can't attach a relation to another relation, so each fact becomes a `Statement` node instead of a bare edge:
```
(Person)-[:SUBJECT]->(Statement {relation_type, editorial_status})-[:OBJECT]->(Territory)
(Statement)-[:HAS_ATTRIBUTE]->(...)
(Statement)-[:SOURCED_BY]->(Source)
```
This mirrors Wikidata's internal statement-based model and is what allows qualifiers and references to attach cleanly.

**Sourcing granularity:** default at the `Statement` level (covers most facts). Only promote specific attributes (dates being the recurring case) into their own sub-statement node when genuinely disputed — full per-attribute sourcing by default was judged as overkill, even Wikidata doesn't do this.

**Source as first-class node**, not just a field — enables queries like "what else does this source attest to" later.

**Default attributes every Statement carries:** evidence source(s), editorial status, revision/contributor history.

**Dates:** use **EDTF (Extended Date/Time Format)** rather than inventing notation — it already handles unspecified digits, "one of several possible dates," ranges, and uncertainty qualifiers. Add a **calendar system field** (Gregorian/Julian/Hijri/etc.) alongside precision, since Julian–Gregorian mismatches and non-Western calendars are unavoidable at global scope.

---

## Schema Governance

- **Meta-model is fixed in code** (Entity, Statement, Attribute, Source, EditorialStatus — the plumbing).
- **Relation/entity *types* are data, not code** — a governed type registry, extensible without deployment.
- Type changes go through the **same proposal → review → approval workflow as data facts**, not a separate system — less to build, and keeps both processes consistent.
- **Additive-only changes** by default (new optional attributes fine; renaming/removing triggers explicit migration review) — avoids silent breakage without needing a full migration engine yet.
- Single trusted schema admin is fine as a starting constraint; worth planning how this delegates later (Wikidata-style property-proposal model) without redesigning the core now.

---

## Community & Review Model

- **Open, wiki-style crowd contributions** — but this requires (in order of build priority):
  1. Revision history per fact
  2. Proposal → review → published states (not raw overwrite)
  3. A "disputed" state that preserves competing sourced claims rather than silently resolving them
  4. A clear content license (likely CC BY-SA, mixing CC0 Wikidata imports with community edits)
- **Recommendation: launch curated/seeded, open editing later** — a wiki with little content and no visitors doesn't attract good contributors; bootstrap like Wikidata did, via bulk import + curation first.

---

## Query → Transform → Display Pipelines

A shareable, forkable "social layer" around saved analysis pipelines — the most ambitious and highest-UX-risk part of the project.

- **Typed dataflow blocks**, each with a fixed input/output shape (like Node-RED/Blueprints) — the UI only offers valid connections, preventing broken pipelines by construction.
- **v1 query stage should stay shallow**, form-like filters (entity type, one-hop relation filter, date-range/attribute predicate) rather than full graph pattern matching — the Roman Emperors example doesn't need more than this. Multi-hop path queries deferred to a later block version.
- **Transform stage** ≈ pivot-table mental model (group/aggregate) — reuse familiar UX (Airtable/Tableau-like), don't invent new interaction patterns.
- **Display stage**: constrain chart type choices to whatever the transform's output shape supports; consider a declarative grammar (Vega-Lite-style) to avoid bespoke glue per chart type.
- **Block contracts are versioned the same way as schema** — additive-only; breaking changes get a new block version so old pipelines don't silently break.
- **Snapshot/pin data versions behind shared pipelines** — since facts are never fixed, a citation needs a reproducible state, not a live-changing query result.
- **Sequencing:** don't launch with an open builder. Launch with a handful of polished, hand-built example pipelines demonstrating the payoff → let users fork/tweak parameters → only build the full open builder once real usage shows what people actually want to build.

---

## Launch Scope Recommendations

- **First vertical slice:** parallel regional timelines (not all four use cases at once).
- **Fixed starter type set** (Person, Territory/Polity, Dynasty, Denomination, Event, Source) rather than building the schema-admin UI before there's real content to justify it.
- **Deliberately over-curate 3–4 regions deeply** (even though region selection is flexible from day one) so demo paths reliably show rich data rather than empty timelines.
- **Statement-level sourcing by default**, sub-statement promotion only where disputed.

---

## Open Questions Still to Resolve

- Should the review queue (for facts and schema proposals alike) be a public talk-page-style queue or a private admin inbox at this stage?
- Timeline display semantics: one row per entity (Gantt-style reign bars) vs. plotting ranges as points on a shared regional axis?
- How schema-admin authority evolves/delegates as the contributor base grows.
