import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Entry} from "../../model/entry.model";
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {TagComponent} from "../tag/tag.component";
import {calculateMaxSpanningDateRange} from "../../util/date-range.utils";
import {DatePipe, NgClass, NgForOf} from "@angular/common";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";

interface TableEntryRepresentation extends Entry {
  start?: string,
  end?: string,
  _original: Entry
}

export interface EntriesTableAction {
  fn: (e: Entry) => void,
  icon: IconDefinition,
  color?: 'warn' | 'success' | 'danger'
}

export interface EntriesTableSearch {
  title?: string,
  start?: string,
  end?: string
}

@Component({
  standalone: true,
  selector: 'chronos-entries-table',
  templateUrl: './entries-table.component.html',
  styleUrls: ['./entries-table.component.scss'],
  imports: [
    FormsModule,
    FontAwesomeModule,
    TagComponent,
    DatePipe,
    NgForOf,
    NgClass
  ]
})
export class EntriesTableComponent {

  @Output()
  rowClick: EventEmitter<Entry> = new EventEmitter<Entry>();

  @Output()
  searchChange: EventEmitter<EntriesTableSearch> = new EventEmitter<EntriesTableSearch>();

  @Input()
  search: EntriesTableSearch = {};

  @Input()
  actions: Array<EntriesTableAction> = [];

  @Input()
  set entries(entries: Array<Entry> | null) {
    this.viewEntries = (entries || []).map(entry => {
      const maxSpanningDateRange = calculateMaxSpanningDateRange(entry.dateRanges);
      return {
        ...entry,
        start: maxSpanningDateRange.start,
        end: maxSpanningDateRange.end,
        _original: entry
      }
    })
  }

  protected viewEntries: Array<TableEntryRepresentation> = [];

  set dateQuery(q: string) {
    const dates = q.split('-')
    this.search.start = dates[0];
    this.search.end = dates[1];
  }

  get dateQuery() {
    const start = this.search.start || '';
    const end = this.search.end || '';
    const separator = (start && end) ? '-' : '';
    return `${start}${separator}${end}`;
  }

}
