import { DataResponseDTO } from "src/app/common/model/data/response/data-response.dto";
import { GraphData } from "../model/graph-data.model";
import { GraphNode } from "../model/graph-node.model";
import { GraphLink } from "../model/graph-link.model";

export class NetworkGraphMapper {

  public static mapDataResponseToGraphData(dataResponse: DataResponseDTO): GraphData {
    const nodes: GraphNode[] = dataResponse.entries.map(entry => ({
        _element: entry,
        id: entry.elementId,
        label: entry.attributes['key'] || '',
        type: entry.labels[0] || '',
        properties: entry.attributes,
    }));

    const links: GraphLink[] = dataResponse.relations.map(relation => ({
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