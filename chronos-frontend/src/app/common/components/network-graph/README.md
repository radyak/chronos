# Neo4j-style Graph Visualisation — Angular + D3

A force-directed property-graph component that mimics Neo4j Browser,
with full **two-way data binding** between the D3 canvas and the Angular model.

---

## Files

| File | Purpose |
|------|---------|
| `graph.models.ts`       | TypeScript interfaces (`GraphNode`, `GraphLink`, `GraphData`) |
| `graph.data.ts`         | Example seed data (Matrix movie graph) |
| `graph-viz.component.ts`| Core D3 component with detail panel + two-way binding |
| `graph-demo.component.ts`| Demo host showing live JSON snapshot |
| `graph.module.ts`       | NgModule wiring |

---

## Setup

```bash
npm install d3 @types/d3
```

Drop the files into your Angular project and add `GraphModule` (or standalone imports) to your `AppModule`.

---

## Two-way binding explained

```html
<app-graph-viz
  [graphData]="graph"
  (graphDataChange)="graph = $event">
</app-graph-viz>
```

| Direction | Mechanism | What happens |
|-----------|-----------|-------------|
| Angular → D3 | `[graphData]` input + `ngOnChanges` | Any programmatic data change re-builds the simulation |
| D3 panel → Angular | `(graphDataChange)` output | Editing a node's label/property in the side panel emits the updated object back to the host |

Because `GraphVizComponent` uses `ChangeDetectionStrategy.OnPush`, change detection is only triggered when the reference to `graphData` changes — the component calls `this.graphDataChange.emit({ ...this.graphData })` (a shallow copy) to ensure Angular picks up the mutation.

---

## Customisation

### Add node types / colours
Edit the `TYPE_COLORS` map in `graph-viz.component.ts`:
```ts
const TYPE_COLORS: Record<string, string> = {
  Movie:  '#e8a838',
  Person: '#6dcfb8',
  Company:'#f472b6',   // add your own
  Default:'#a78bfa',
};
```

### Change physics
Adjust force parameters in `buildGraph()`:
```ts
d3.forceLink(…).distance(120)     // link length
d3.forceManyBody().strength(-400) // repulsion
d3.forceCollide(NODE_RADIUS + 10) // min node gap
```

### Responding to external data changes
Because `ngOnChanges` watches `graphData`, simply replace the reference in the host:
```ts
this.graph = { ...newData };  // triggers rebuild
```

---

## Known limitations / next steps
- Relationship editing (add/remove links) not yet implemented
- No undo/redo stack
- Large graphs (>500 nodes) may need WebGL renderer (e.g. `d3-force-3d` + `three.js`)
