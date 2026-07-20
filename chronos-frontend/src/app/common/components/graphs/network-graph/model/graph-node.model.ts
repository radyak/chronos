import * as d3 from 'd3';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';

export interface GraphNode extends d3.SimulationNodeDatum {
  _element?: EntryDTO; // Reference to the original data entry
  id: string;
  label: string;
  type: string;       // maps to a color/icon group
  properties: Record<string, string | number>;
}
