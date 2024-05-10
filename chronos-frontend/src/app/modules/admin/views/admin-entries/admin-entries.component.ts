import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {Observable, of} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AdminConfirmService} from "../../services/admin-confirm.service";
import {calculateMaxSpanningDateRange} from "../../../../util/date-range.utils";
import {DateRange} from "../../../../model/date-range.model";
import {QueryDrivenComponent} from "../../../../common/query-driven-component.directive";


@Component({
  selector: 'chronos-admin-entries',
  templateUrl: './admin-entries.component.html',
  styleUrls: ['./admin-entries.component.scss']
})
export class AdminEntriesComponent extends QueryDrivenComponent {

  entries$: Observable<Array<Entry>> = of([]);

  titleQuery: string = '';
  dateQuery: string = '';

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;
  deleteIcon = faTrash;

  constructor(private adminEntriesService: AdminEntriesService,
              protected override router: Router,
              protected override route: ActivatedRoute,
              private confirmService: AdminConfirmService) {
    super(router, route);
  }

  override search(): void {
    this.entries$ = this.adminEntriesService.allEntries(this.toParams())
  }

  override toClassFields(params: Params) {
    const from = params['from']
    const to = params['to']
    this.titleQuery = params['title'];
    this.dateQuery = (!from && !to) ? '' : `${params['from'] || ''}-${params['to'] || ''}`
  }

  override toParams(): Params {
    const dates = this.dateQuery.split('-')
    return {
      title: this.titleQuery,
      from: dates[0] || null,
      to: dates[1] || null
    };
  }

  editEntry(entry: Entry): void {
    this.router.navigate([entry.id], {relativeTo: this.route});
  }

  newEntry(): void {
    this.router.navigate(['new'], {relativeTo: this.route});
  }

  deleteEntry(entry: Entry): void {
    this.confirmService.confirm(
      "Confirm deleting entry",
      `Do you want to delete entry ${entry.title}?`
    )
    .then(() => {
      this.adminEntriesService.deleteEntry(entry).subscribe(() => this.search())
    });
  }

  entryDateSpan(entry: Entry): DateRange {
    return calculateMaxSpanningDateRange(entry.dateRanges);
  }

}

