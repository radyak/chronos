import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {debounceTime, filter, from, map, Subject} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {AdminTagsService} from "../../services/admin-tags.service";
import {Tag} from "../../../../model/tag.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {DateRange} from "../../../../model/date-range.model";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {RelationService} from "../../../../service/relation-service";
import {Relation} from "../../../../model/relation.model";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AdminEditDateModal} from "../../components/admin-edit-date-modal/admin-edit-date-modal";
import {AdminEditRelationModal} from "../../components/admin-edit-relation-modal/admin-edit-relation-modal";


@Component({
  selector: 'chronos-admin-entry',
  templateUrl: './admin-entry.component.html',
  styleUrls: ['./admin-entry.component.scss']
})
export class AdminEntryComponent implements OnInit {

  wikipediaSummary?: WikipediaSummary;
  availableTags: Array<Tag> = [];
  currentEntry?: Entry;

  wikipediaPageInputVisible = false;

  wikipediaTitleSearch$ = new Subject<string | undefined>();

  newIcon = faPlus;
  deleteIcon = faTrash;

  constructor(private adminEntriesService: AdminEntriesService,
              private adminTagsService: AdminTagsService,
              private relationService: RelationService,
              private modal: NgbModal,
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


  /**
   *  SECTION: Customization
   */

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


  /**
   *  SECTION: Manage entry
   */

  saveEntry(): void {
    if (!this.currentEntry) {
      return
    }
    this.adminEntriesService.saveEntry(this.currentEntry).subscribe(() => {
      this.goBack();
    });
  }

  deleteEntry(entry: Entry): void {
    this.adminEntriesService.deleteEntry(entry).subscribe(() => {
      this.goBack();
    })
  }

  cancelEditEntry(): void {
    this.goBack();
  }


  /**
   *  SECTION: Wiki summary
   */

  searchSummary() {
    this.wikipediaTitleSearch$.next(this.currentEntry?.wikipediaPage || this.currentEntry?.title)
  }

  private findWikipediaSummary(title?: string): void {
    title = title || this.currentEntry?.title;
    this.adminEntriesService.findWikipediaSummary(title!).subscribe(wikipediaSummary => {
      this.wikipediaSummary = wikipediaSummary;
    })
  }


  /**
   *  SECTION: Date ranges
   */

  addDateRange() {
    const newDate = {
      start: "0000-01-01",
      end: "0000-01-01"
    };
    this.currentEntry?.dateRanges?.push(newDate);
    this.editDateRange(newDate);
  }

  editDateRange(dateRange: DateRange) {
    const modalRef = this.modal.open(AdminEditDateModal, { ariaLabelledBy: 'modal-basic-title' })
    modalRef.componentInstance.dateRange = dateRange;
    modalRef.result.then((editedDateRange: DateRange) => {
      const index = (this.currentEntry?.dateRanges || []).indexOf(dateRange);
      if (index >= 0) {
        this.currentEntry!.dateRanges![index] = editedDateRange;
      }
    }, (error) => {
      // Nothing to do
    });
  }

  deleteDateRange(dateRange: DateRange) {
    this.currentEntry?.dateRanges?.splice(this.currentEntry?.dateRanges?.indexOf(dateRange), 1);
  }


  /**
   *  SECTION: Relations
   */

  addRelation() {
    const relation: Relation = {
      type: null as any,
      from: this.currentEntry,
      fromId: this.currentEntry?.id || 0,
      toId: 0
    };
    this.currentEntry?.relations?.push(relation);
    this.editRelation(relation);
  }

  editRelation(relation: Relation) {
    const modalRef = this.modal.open(AdminEditRelationModal, { ariaLabelledBy: 'modal-basic-title' })
    modalRef.componentInstance.relation = relation;
    modalRef.componentInstance.entry = this.currentEntry;
    modalRef.result.then((editedRelation: Relation) => {
      const index = (this.currentEntry?.relations || []).indexOf(relation);
      if (index >= 0) {
        this.currentEntry!.relations![index] = editedRelation;
      }
    }, (error) => {
      // Nothing to do
    });
  }

  deleteRelation(relation: Relation) {
    this.currentEntry?.relations?.splice(this.currentEntry?.relations?.indexOf(relation), 1);
  }

  relationLabel(relation: Relation): string {
    return this.relationService.getRelationDescription(
      (relation.fromId === this.currentEntry?.id && !!relation.type?.label ?
        relation.type?.label : relation.type?.inverseRelationLabel) || '-',
      relation.value
    );
  }

  relationOtherEntry(relation: Relation): Entry | undefined {
    return (relation.fromId === this.currentEntry?.id ? relation.to : relation.from);
  }


  /**
   *  SECTION: Navigation
   */

  goTo(entry?: Entry): void {
    if (!entry) {
      return;
    }
    this.router.navigate(['..', entry.id], {
      relativeTo: this.route
    });
  }

  goBack(): void {
    this.router.navigate(['..'], { relativeTo: this.route });
  }

}

