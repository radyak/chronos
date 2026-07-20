import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component, effect, ElementRef,
  input,
  OnDestroy,
  ViewChild
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import * as d3 from 'd3';
import { GraphData } from './model/graph-data.model';
import { GraphLink } from './model/graph-link.model';
import { GraphNode } from './model/graph-node.model';
import { defaultNetworkGraphConfig } from './model/network-graph.config';


@Component({
  selector: 'chronos-network-graph',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './network-graph.component.html',
  styleUrls: ['./network-graph.component.scss'],
})
export class NetworkGraphComponent implements OnDestroy {

  /** Input: full graph data */
  public readonly graphData = input.required<GraphData>();
  public readonly typeColorMap = input<Record<string, string>>();

  @ViewChild('host',        { static: true }) hostRef!: ElementRef<HTMLDivElement>;
  @ViewChild('svg',         { static: true }) svgRef!:  ElementRef<SVGSVGElement>;
  @ViewChild('zoomLayer',   { static: true }) zoomRef!: ElementRef<SVGGElement>;
  @ViewChild('linksLayer',  { static: true }) linksRef!:ElementRef<SVGGElement>;
  @ViewChild('nodesLayer',  { static: true }) nodesRef!:ElementRef<SVGGElement>;
  @ViewChild('labelsLayer', { static: true }) lblRef!:  ElementRef<SVGGElement>;

  config = defaultNetworkGraphConfig;

  selectedNode: GraphNode | null = null;

  /** Unique relationship type names (used for SVG marker defs via *ngFor) */
  get linkTypes(): string[] {
    return [...new Set(this.graphData().links.map(l => l.type) ?? [])];
  }

  private simulation!: d3.Simulation<GraphNode, GraphLink>;
  private resizeObserver!: ResizeObserver;

  constructor() {
    effect(() => {
      const graphData = this.graphData();
      const typeColorMap = this.typeColorMap();
      if (graphData && typeColorMap) {
        this.buildGraph();
      }
    });
  }

  // ─── Lifecycle ────────────────────────────────────────────────────────────

  public ngOnDestroy(): void {
    this.simulation?.stop();
    this.resizeObserver?.disconnect();
  }

  // ─── Graph construction ───────────────────────────────────────────────────

  private buildGraph(): void {
    const hostEl = this.hostRef.nativeElement;
    const w = hostEl.clientWidth;
    const h = hostEl.clientHeight;

    const svg = d3.select(this.svgRef.nativeElement);

    // Zoom behaviour
    if (this.config.zoom.enabled) {
      const zoom = d3.zoom<SVGSVGElement, unknown>()
        .scaleExtent([0.2, 4])
        .on('zoom', (event) => {
          d3.select(this.zoomRef.nativeElement)
            .attr('transform', event.transform);
        });
      svg.call(zoom as any);
    }

    // Deep-copy data so D3 mutations don't corrupt the Angular model
    const nodes: GraphNode[] = this.graphData().nodes.map(n => ({ ...n }));
    const links: GraphLink[] = this.graphData().links.map(l => ({ ...l }));

    // ── Simulation ──────────────────────────────────────────────────────────
    this.simulation?.stop();
    this.simulation = d3.forceSimulation<GraphNode>(nodes)
      .force('link', d3.forceLink<GraphNode, GraphLink>(links)
        .id(d => d.id).distance(120))
      .force('charge', d3.forceManyBody().strength(-400))
      .force('center', d3.forceCenter(w / 2, h / 2))
      .force('collision', d3.forceCollide(this.config.nodes.radius + 10));

    // ── Links ───────────────────────────────────────────────────────────────
    const linkSel = d3.select(this.linksRef.nativeElement)
      .selectAll<SVGLineElement, GraphLink>('line')
      .data(links, d => d.id)
      .join('line')
      .attr('stroke', this.config.links.color)
      .attr('stroke-width', this.config.links.width)
      .attr('stroke-opacity', this.config.links.opacity)
      .attr('marker-end', d => `url(#arrow-${d.type})`);

    // Link labels
    const linkLabelSel = d3.select(this.linksRef.nativeElement)
      .selectAll<SVGTextElement, GraphLink>('text')
      .data(links, d => d.id)
      .join('text')
      .attr('fill', this.config.links.text.color || this.config.text.color)
      .attr('font-size', this.config.links.text.fontSize || this.config.text.fontSize)
      .attr('text-anchor', 'middle')
      .attr('dominant-baseline', 'middle')
      .text(d => d.type);

    // ── Nodes ───────────────────────────────────────────────────────────────
    const nodeSel = d3.select(this.nodesRef.nativeElement)
      .selectAll<SVGCircleElement, GraphNode>('circle')
      .data(nodes, d => d.id)
      .join('circle')
      .attr('r', this.config.nodes.radius)
      .attr('fill', d => this.nodeColor(d))
      .attr('stroke', this.config.nodes.stroke)
      .attr('stroke-width', this.config.nodes.strokeWidth)
      .style('cursor', 'pointer')
      .call(this.dragBehaviour(this.simulation) as any)
      .on('click', (_event, d) => {
          // Map D3 node back to original Angular model node
          this.selectedNode = this.graphData().nodes.find(n => n.id === d.id) ?? null;
          console.log('Selected node:', this.selectedNode);
      });

    // Node labels
    const labelSel = d3.select(this.lblRef.nativeElement)
      .selectAll<SVGTextElement, GraphNode>('text')
      .data(nodes, d => d.id)
      .join('text')
      .attr('text-anchor', 'middle')
      .attr('dominant-baseline', 'middle')
      .attr('fill', this.config.text.color)
      .attr('font-size', this.config.text.fontSize)
      .attr('font-weight', '700')
      .attr('pointer-events', 'none')
      .text(d => this.truncate(d.label, 12));

    // ── Tick ────────────────────────────────────────────────────────────────
    this.simulation.on('tick', () => {
      linkSel
        .attr('x1', d => (d.source as GraphNode).x!)
        .attr('y1', d => (d.source as GraphNode).y!)
        .attr('x2', d => (d.target as GraphNode).x!)
        .attr('y2', d => (d.target as GraphNode).y!);

      linkLabelSel
        .attr('x', d => ((d.source as GraphNode).x! + (d.target as GraphNode).x!) / 2)
        .attr('y', d => ((d.source as GraphNode).y! + (d.target as GraphNode).y!) / 2);

      nodeSel
        .attr('cx', d => d.x!)
        .attr('cy', d => d.y!);

      labelSel
        .attr('x', d => d.x!)
        .attr('y', d => d.y!);
    });

    // Re-sync label text when Angular model label changes
    // (called from onNodeLabelChange)
    (this as any)._refreshLabels = () => {
      labelSel.text(d => {
        const updated = this.graphData().nodes.find(n => n.id === d.id);
        return this.truncate(updated?.label ?? d.label, 12);
      });
    };
  }

  // ─── Drag behaviour ───────────────────────────────────────────────────────

  private dragBehaviour(sim: d3.Simulation<GraphNode, GraphLink>) {
    const drag = d3.drag<SVGCircleElement, GraphNode>();
    if (!this.config.drag.enabled) {
      return drag;
    }
    return drag
        .on('start', (event, d) => {
          if (!event.active) sim.alphaTarget(0.3).restart();
          d.fx = d.x; d.fy = d.y;
        })
        .on('drag', (event, d) => { d.fx = event.x; d.fy = event.y; })
        .on('end',  (event, d) => {
          if (!event.active) sim.alphaTarget(0);
          d.fx = null; d.fy = null;
        });
  }

  // ─── Template helpers ─────────────────────────────────────────────────────

  private nodeColor(node: GraphNode): string {
    var color = this.getTypeColor(node.type);
    var rgbColor = d3.rgb(color).darker(this.config.nodes.dim).formatRgb();
    return rgbColor;
  }

  private truncate(text: string, max: number): string {
    return text.length > max ? text.slice(0, max - 1) + '…' : text;
  }

  private getTypeColor(type: string): string {
    // {
    //   Movie:  '#e8a838',
    //   Person: '#6dcfb8',
    //   Default:'#a78bfa',
    // }
    return this.typeColorMap()?.[type] ?? '#a78bfa';
  }
}
