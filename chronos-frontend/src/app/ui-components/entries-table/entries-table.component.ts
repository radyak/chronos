import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Entry} from "../../model/entry.model";
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {TagComponent} from "../tag/tag.component";
import {calculateMaxSpanningDateRange} from "../../util/date-range.utils";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faClose, faSearch} from "@fortawesome/free-solid-svg-icons";
import {tagMatches} from "../../util/tag-match.function";

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
    NgClass,
    NgIf
  ]
})
export class EntriesTableComponent {

  searchIcon = faSearch;
  clearIcon = faClose;

  @Output()
  rowClick: EventEmitter<Entry> = new EventEmitter<Entry>();

  @Input()
  search: EntriesTableSearch = {};

  @Input()
  searchable = true;

  @Output()
  submit: EventEmitter<void> = new EventEmitter<void>();

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

  protected tagQuery = '';

  clearTitle() {
    this.search.title = '';
    this.submit.emit();
  }

  clearDateQuery() {
    this.search.start = '';
    this.search.end = '';
    this.submit.emit();
  }

  filteredEntries(): Array<TableEntryRepresentation> {
    return (this.viewEntries || []).filter(entry =>
      entry.tags?.some(tag => tagMatches(this.tagQuery, tag))
    );
  }

}
