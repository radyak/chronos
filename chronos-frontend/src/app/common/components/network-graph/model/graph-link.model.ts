import * as d3 from 'd3';
import { GraphNode } from './graph-node.model';
import { RelationDTO } from 'src/app/common/model/data/response/relation.dto';

export interface GraphLink extends d3.SimulationLinkDatum<GraphNode> {
  _element?: RelationDTO; // Reference to the original data entry
  id: string;
  type: string;       // relationship label
  source: string | GraphNode;
  target: string | GraphNode;
  properties?: Record<string, string | number>;
}
