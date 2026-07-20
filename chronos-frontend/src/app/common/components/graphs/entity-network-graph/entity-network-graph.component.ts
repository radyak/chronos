import { Component, computed, input } from '@angular/core';
import { NetworkGraphComponent } from '../network-graph';
import { EntityNetworkGraphData } from './entity-network-graph-data.model';
import { EntityNetworkGraphMapper } from './entity-network-graph-mapper';
import { SchemaTypeDTO } from 'src/app/common/model/schema/type.dto';
import { SchemaTypeAO } from 'src/app/common/model/schema/admin/type.ao';

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

}
