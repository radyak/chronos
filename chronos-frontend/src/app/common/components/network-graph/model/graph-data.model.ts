import { GraphLink } from './graph-link.model';
import { GraphNode } from './graph-node.model';

export interface GraphData {
  nodes: GraphNode[];
  links: GraphLink[];
}
