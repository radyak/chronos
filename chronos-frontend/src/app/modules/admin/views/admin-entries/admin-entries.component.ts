import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {debounceTime, filter, map, Observable, of, Subject, switchMap} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {AdminTagsService} from "../../services/admin-tags.service";
import {Tag} from "../../../../model/tag.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {DateRange, DateRangeType} from "../../../../model/date-range.model";


@Component({
  selector: 'chronos-admin-entries',
  templateUrl: './admin-entries.component.html',
  styleUrls: ['./admin-entries.component.scss']
})
export class AdminEntriesComponent implements OnInit {

  entries$: Observable<Array<Entry>> = of([]);
  wikipediaSummary?: WikipediaSummary;
  availableTags: Array<Tag> = [];
  currentEntry?: Entry;
  currentEntryDateRange?: DateRange;
  searchEntryTitle: string = '';

  wikipediaPageInputVisible = false;

  wikipediaTitleSearch$ = new Subject<string | undefined>();

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;
  deleteIcon = faTrash;

  constructor(private adminEntriesService: AdminEntriesService,
              private adminTagsService: AdminTagsService) {
    this.wikipediaTitleSearch$.pipe(
      filter(title => (!!title && title.length >= 3)),
      map(title => title!.trim()),
      debounceTime(700),
      switchMap(title => {
        return this.adminEntriesService.findWikipediaSummary(title!)
      })
    )
    .subscribe(wikipediaSummary => {
      this.wikipediaSummary = wikipediaSummary;
    })
  }

  ngOnInit(): void {
    this.entries$ = this.adminEntriesService.allEntries();
    this.adminTagsService.allTags().subscribe(tags => {
      this.availableTags = tags;
    })
  }

  editEntry(entry: Entry): void {
    this.currentEntry = entry;
    this.searchSummary();
  }

  newEntry(): void {
    this.currentEntry = {
      title: "",
      dateRanges: [
        {
          start: "0000-01-01",
          end: "0000-01-01"
        }
      ]
    };
  }

  saveEntry(): void {
    if (!this.currentEntry) {
      return
    }

    this.adminEntriesService.saveEntry(this.currentEntry).subscribe(() => {
      delete this.currentEntry;
      this.entries$ = this.adminEntriesService.allEntries();
    })
  }

  cancelEditEntry(): void {
    delete this.currentEntry;
  }

  deleteEntry(entry: Entry): void {
    this.adminEntriesService.deleteEntry(entry).subscribe(() => {
      delete this.currentEntry;
      this.entries$ = this.adminEntriesService.allEntries();
    })
  }

  filterEntry(entry: Entry): boolean {
    return !!entry.title && entry.title.toLowerCase().includes(this.searchEntryTitle.toLowerCase())
  }

  removeTag(tag: Tag): void {
    this.currentEntry?.tags?.splice(this.currentEntry?.tags?.indexOf(tag), 1);
  }

  searchSummary() {
    if (this.currentEntry?.wikipediaPage) {
      this.wikipediaTitleSearch$.next(this.currentEntry?.wikipediaPage);
    } else {
      this.wikipediaTitleSearch$.next(this.currentEntry?.title)
    }
  }

  editDateRange(dateRange: DateRange) {
    this.currentEntryDateRange = dateRange;
  }

  deleteDateRange(dateRange: DateRange) {
    this.currentEntry?.dateRanges?.splice(this.currentEntry?.dateRanges?.indexOf(dateRange), 1);
    delete this.currentEntryDateRange;
  }

  addDateRange() {
    const newDate = {
      start: "0000-01-01",
      end: "0000-01-01"
    };
    this.currentEntry?.dateRanges?.push(newDate);
    this.editDateRange(newDate);
  }

  allDateRangeTypes(): Array<DateRangeType> {
    return Object.values(DateRangeType);
  }

}

