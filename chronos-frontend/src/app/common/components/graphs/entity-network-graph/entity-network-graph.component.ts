import { Component, computed, input, signal } from '@angular/core';
import { SchemaTypeAO } from 'src/app/common/model/schema/admin/type.ao';
import { GraphLink, GraphNode, NetworkGraphComponent } from '../network-graph';
import { EntityNetworkGraphData } from './entity-network-graph-data.model';
import { EntityNetworkGraphMapper } from './entity-network-graph-mapper';
import { RelationDTO } from 'src/app/common/model/data/response/relation.dto';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';

@Component({
  selector: 'chronos-entity-network-graph',
  imports: [
    NetworkGraphComponent
  ],
  templateUrl: './entity-network-graph.component.html',
  styleUrl: './entity-network-graph.component.scss',
})
export class EntityNetworkGraphComponent {
  // Inputs
  public readonly data = input.required<EntityNetworkGraphData | undefined>();
  public readonly schema = input.required<SchemaTypeAO[] | undefined>();

  // Signals
  protected readonly selectedElements = signal<Array<EntryDTO | RelationDTO>>([]);

  // Derived Signals
  protected graphData = computed(() => {
    const data = this.data();
    if (!data) {
      return { nodes: [], links: [] };
    }
    return EntityNetworkGraphMapper.mapToGraphData(data);
  });

  protected typeColorMap = computed(() => {
    const types = this.schema();
    if (!types || types.length === 0) {
      return {};
    }
    return types.reduce((map, type) => {
      map[type.key!] = type.color || '#a78bfa';
      return map;
    }, {} as Record<string, string>);
  });

  protected onElementClick(element: EntryDTO | RelationDTO): void {
    this.selectedElements.update((selected) => [...selected, element]);
  }
}
