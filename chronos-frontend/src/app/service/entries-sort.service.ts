import {Injectable} from "@angular/core";
import {Entry} from "../model/entry.model";
import {getEarliestStartDateRange} from "../util/date-range.utils";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faSort, faSortDown, faSortUp} from "@fortawesome/free-solid-svg-icons";


export interface EntrySortableProperty {
  key: 'title' | 'subTitle' | 'start' | 'end';
  label: string;
  compare: (e1: Entry, e2: Entry) => number;
}

export type SortDirection = 'asc' | 'desc';

export interface SortConfig {
  property: EntrySortableProperty,
  direction: SortDirection
}


@Injectable({
  providedIn: 'root'
})
export class EntriesSortService {

  private readonly sortOptions: Array<EntrySortableProperty> = [
    {
      key: 'title',
      label: 'Title',
      compare: (e1: Entry, e2: Entry) => {
        if ((e1.title || '') < (e2.title || ''))
          return 1;
        if ((e1.title || '') > (e2.title || ''))
          return -1;
        return 0;
      }
    },
    {
      key: 'subTitle',
      label: 'Subtitle',
      compare: (e1: Entry, e2: Entry) => {
        if ((e1.subTitle || '') < (e2.subTitle || ''))
          return 1;
        if ((e1.subTitle || '') > (e2.subTitle || ''))
          return -1;
        return 0;
      }
    },
    {
      key: 'start',
      label: 'Earliest start date',
      compare: (e1: Entry, e2: Entry) => {
        return (getEarliestStartDateRange(e1.dateRanges).start! > getEarliestStartDateRange(e2.dateRanges).start! ? 1 : -1)
      }
    },
    {
      key: 'end',
      label: 'Latest end date',
      compare: (e1: Entry, e2: Entry) => {
        return (getEarliestStartDateRange(e1.dateRanges).end! > getEarliestStartDateRange(e2.dateRanges).end! ? 1 : -1)
      }
    }
  ];

  public allSortProperties(): Array<EntrySortableProperty> {
    return this.sortOptions;
  }

  public findSortPropertyByKey(key: string): EntrySortableProperty | undefined {
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

  public sort(entries: Array<Entry>, sort?: SortConfig): Array<Entry> {
    return entries.sort((e1, e2) => {
        if (!sort) {
          return 0;
        }
        return sort.property.compare(e1, e2) * (sort.direction === 'asc' ? 1 : -1)
      }
    )
  }

}
