import {Injectable} from "@angular/core";
import {getEarliestStartDateRange} from "../util/date-range.utils";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faSort, faSortDown, faSortUp} from "@fortawesome/free-solid-svg-icons";
import { Entity } from "../model/domain/entity.model";


export interface EntitySortableProperty {
  key: 'key' | 'start' | 'end';
  label: string;
  compare: (e1: Entity, e2: Entity) => number;
}

export type SortDirection = 'asc' | 'desc';

export interface SortConfig {
  property: EntitySortableProperty,
  direction: SortDirection
}

function compareStrings(a: string = '', b: string = ''): number {
  return a.localeCompare(b);
}


@Injectable({
  providedIn: 'root'
})
export class EntitiesSortService {

  private readonly sortOptions: Array<EntitySortableProperty> = [
    {
      key: 'key',
      label: 'Key',
      compare: (e1: Entity, e2: Entity) => {
        return (e1.key || '').localeCompare(e2.key || '');
      }
    },
    {
      key: 'start',
      label: 'Earliest start date',
      compare: (e1: Entity, e2: Entity) => {
        return (e1.from || '').localeCompare(e2.from || '');
      }
    },
    {
      key: 'end',
      label: 'Latest end date',
      compare: (e1: Entity, e2: Entity) => {
        return (e1.to || '').localeCompare(e2.to || '');
      }
    }
  ];

  public allSortProperties(): Array<EntitySortableProperty> {
    return this.sortOptions;
  }

  public findSortPropertyByKey(key: string): EntitySortableProperty | undefined {
    return this.sortOptions.find(o => o.key === key)
  }

  public rotateDirection(currentDirection?: SortDirection): SortDirection {
    return currentDirection === 'asc' ? 'desc' : 'asc';
  }

  public sortIcon(direction?: SortDirection): IconDefinition {
    switch (direction) {
      case 'asc': return faSortUp;
      case 'desc': return faSortDown;
      default: return faSort;
    }
  }

  public sort(entries: Array<Entity>, sort?: SortConfig): Array<Entity> {
    return entries.sort((e1, e2) => {
        if (!sort) {
          return 0;
        }
        return sort.property.compare(e1, e2) * (sort.direction === 'asc' ? 1 : -1)
      }
    )
  }

}
