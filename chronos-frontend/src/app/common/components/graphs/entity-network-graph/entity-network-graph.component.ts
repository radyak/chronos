import { Component, computed, input, model } from '@angular/core';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';
import { RelationDTO } from 'src/app/common/model/data/response/relation.dto';
import { SchemaTypeAO } from 'src/app/common/model/schema/admin/type.ao';
import { NetworkGraphComponent } from '../network-graph';
import { EntityNetworkGraphData } from './entity-network-graph-data.model';
import { EntityNetworkGraphMapper } from './entity-network-graph-mapper';

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
  public readonly multipleSelection = input<boolean>(false);
  public readonly entriesSelectable = input<boolean>(true);
  public readonly relationsSelectable = input<boolean>(true);

  // Signals
  public readonly selectedElements = model<Array<EntryDTO | RelationDTO>>([]);

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
    if (!this.entriesSelectable() && 'labels' in element) {
      return;
    }
    if (!this.relationsSelectable() && 'type' in element) {
      return;
    }
    if (this.multipleSelection()) {
      const selected = this.selectedElements();
      if (!selected.includes(element)) {
        // Not yet selected -> add to selection
        this.selectedElements.set([...selected, element]);
      } else {
        // Already selected -> remove from selection
        this.selectedElements.set(selected.filter(e => e !== element));
      }
    } else {
      this.selectedElements.set([element]);
    }
  }
}
