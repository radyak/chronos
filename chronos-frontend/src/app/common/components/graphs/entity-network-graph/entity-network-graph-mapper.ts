import { DataResponseDTO } from "src/app/common/model/data/response/data-response.dto";
import { GraphData, GraphLink, GraphNode } from "../network-graph";
import { EntityNetworkGraphData } from "./entity-network-graph-data.model";

export class EntityNetworkGraphMapper {

  public static mapToGraphData(data: EntityNetworkGraphData): GraphData {
    const nodes: GraphNode[] = data.entries.map(entry => ({
        _element: entry,
        id: entry.elementId,
        label: entry.attributes['key'] || '',
        type: entry.labels[0] || '',
        properties: entry.attributes,
    }));

    const links: GraphLink[] = data.relations.map(relation => ({
        _element: relation,
        id: relation.elementId,
        type: relation.type,
        source: relation.startElementId,
        target: relation.endElementId,
        properties: relation.attributes,
    }));

    return { nodes, links };
  }
}