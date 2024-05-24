import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {debounceTime, filter, map, Subject} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {AdminTagsService} from "../../services/admin-tags.service";
import {Tag} from "../../../../model/tag.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {DateRange, DateRangeType} from "../../../../model/date-range.model";
import {ActivatedRoute, Router} from "@angular/router";


@Component({
  selector: 'chronos-admin-entry',
  templateUrl: './admin-entry.component.html',
  styleUrls: ['./admin-entry.component.scss']
})
export class AdminEntryComponent implements OnInit {

  wikipediaSummary?: WikipediaSummary;
  availableTags: Array<Tag> = [];
  currentEntry?: Entry;
  currentEntryDateRange?: DateRange;

  wikipediaPageInputVisible = false;

  wikipediaTitleSearch$ = new Subject<string | undefined>();

  editIcon = faPenToSquare;
  newIcon = faPlus;
  deleteIcon = faTrash;

  constructor(private adminEntriesService: AdminEntriesService,
              private adminTagsService: AdminTagsService,
              private router: Router,
              private route: ActivatedRoute) {
    this.wikipediaTitleSearch$.pipe(
      filter(title => (!!title && title.length >= 3)),
      map(title => title!.trim()),
      debounceTime(700)
    ).subscribe(title => this.findWikipediaSummary(title))

  }

  ngOnInit(): void {
    const entryId = this.route.snapshot.params['id'];
    if (entryId === 'new') {
      this.currentEntry = {
        title: "",
        tags: [],
        dateRanges: []
      };
    } else {
      this.adminEntriesService.getEntry(entryId).subscribe(entry => {
        this.currentEntry = entry;
        this.findWikipediaSummary(entry.title);
        this.afterLoadEntry();
      })
    }

    this.adminTagsService.allTags().subscribe(tags => {
      this.availableTags = tags;
    });
  }

  protected pageTitle(): string {
    return this.currentEntry?.title ? `"${this.currentEntry?.title}"` : "New Entry";
  }

  protected breadCrumbTitle(): string {
    return this.currentEntry?.title || 'New';
  }

  protected afterLoadEntry(): void {
    // To be overwritten from extending classes
  }

  private findWikipediaSummary(title?: string): void {
    title = title ? title : this.currentEntry?.title;
    this.adminEntriesService.findWikipediaSummary(title!).subscribe(wikipediaSummary => {
      this.wikipediaSummary = wikipediaSummary;
    })
  }

  saveEntry(): void {
    if (!this.currentEntry) {
      return
    }
    this.adminEntriesService.saveEntry(this.currentEntry).subscribe(() => {
      this.goBack();
    });
  }

  cancelEditEntry(): void {
    this.goBack();
  }

  goBack(): void {
    this.router.navigate(['..'], { relativeTo: this.route });
  }

  deleteEntry(entry: Entry): void {
    this.adminEntriesService.deleteEntry(entry).subscribe(() => {
      this.goBack();
    })
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

