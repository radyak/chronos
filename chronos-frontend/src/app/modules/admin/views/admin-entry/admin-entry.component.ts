import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {debounceTime, filter, map, Subject} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {AdminTagsService} from "../../services/admin-tags.service";
import {Tag} from "../../../../model/tag.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {DateRange, DateRangeType} from "../../../../model/date-range.model";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {RelationService} from "../../../../service/relation-service";
import {Relation} from "../../../../model/relation.model";


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
              private relationService: RelationService,
              private router: Router,
              private route: ActivatedRoute) {
    this.wikipediaTitleSearch$.pipe(
      filter(title => (!!title && title.length >= 3)),
      map(title => title!.trim()),
      debounceTime(700)
    ).subscribe(title => this.findWikipediaSummary(title))

  }

  ngOnInit(): void {
    this.setCurrentEntry();
    this.adminTagsService.allTags().subscribe(tags => {
      this.availableTags = tags;
    });
  }

  protected setCurrentEntry(): void {
    this.route.params.subscribe((params: Params) => {
      const entryId = params['id'];
      this.adminEntriesService.getEntry(entryId).subscribe(entry => {
        this.currentEntry = entry;
        this.findWikipediaSummary(entry.title);
        this.afterLoadEntry();
      });
    });
  }

  protected pageTitle(): string {
    return this.currentEntry?.title || '';
  }

  protected breadCrumbTitle(): string {
    return this.currentEntry?.title || '';
  }

  protected afterLoadEntry(): void {
    // To be overwritten from extending classes
    this.adminEntriesService.enrichEntry(this.currentEntry!);
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

  relationLabel(relation: Relation): string {
    return this.relationService.getRelationDescription(
      (relation.fromId === this.currentEntry?.id && !!relation.type.label ?
        relation.type.label : relation.type.inverseRelationLabel) || '-',
      relation.value
    );
  }

  relationOtherEntry(relation: Relation): Entry | undefined {
    return (relation.fromId === this.currentEntry?.id ? relation.to : relation.from);
  }

  goTo(entry?: Entry): void {
    if (!entry) {
      return;
    }
    this.router.navigate(['..', entry.id], {
      relativeTo: this.route
    });
  }
}

