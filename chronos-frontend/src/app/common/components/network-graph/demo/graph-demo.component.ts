import { Component } from '@angular/core';
import { GraphData } from '../model/graph-data.model';
import { EXAMPLE_GRAPH } from './graph.data';
import { NetworkGraphComponent } from '../network-graph.component';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-graph-demo',
  templateUrl: './graph-demo.component.html',
  styleUrls: [
    './graph-demo.component.scss'
  ],
  imports: [
    NetworkGraphComponent,
    JsonPipe
  ]
})
export class GraphDemoComponent {
  graph: GraphData = EXAMPLE_GRAPH;
}
