import {Component} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {Observable, of} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faListCheck, faPenToSquare, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AdminConfirmService} from "../../services/admin-confirm.service";
import {QueryDrivenComponent} from "../../../../common/query-driven-component.directive";
import {EntriesTableAction, EntriesTableSearch} from "../../../../ui-components/entries-table/entries-table.component";
import {AdminBulkActionService, BulkAction} from "../../services/admin-bulk-action.service";


@Component({
  selector: 'chronos-admin-entries',
  templateUrl: './admin-entries.component.html',
  styleUrls: ['./admin-entries.component.scss']
})
export class AdminEntriesComponent extends QueryDrivenComponent {

  newIcon = faPlus;
  batchActionIcon = faListCheck;

  entries$: Observable<Array<Entry>> = of([]);
  selectedEntries: Array<Entry> = [];

  entrySearch: EntriesTableSearch = {};
  tableActions: Array<EntriesTableAction> = [
    {
      fn: (entry: Entry) => this.editEntry(entry),
      icon: faPenToSquare,
    },
    {
      fn: (entry: Entry) => this.deleteEntry(entry),
      icon: faTrash,
      color: 'danger'
    }
  ]

  constructor(private adminEntriesService: AdminEntriesService,
              protected override router: Router,
              protected override route: ActivatedRoute,
              private confirmService: AdminConfirmService,
              private adminBulkActionService: AdminBulkActionService) {
    super(router, route);
  }

  override search(): void {
    this.entries$ = this.adminEntriesService.allEntries(this.toParams())
  }

  override toClassFields(params: Params) {
    this.entrySearch.title = params['title'];
    this.entrySearch.start = params['from'];
    this.entrySearch.end = params['to'];
  }

  override toParams(): Params {
    return {
      title: this.entrySearch.title,
      from: this.entrySearch.start,
      to: this.entrySearch.end
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

  availableBulkActions(): Array<BulkAction> {
    return this.adminBulkActionService.getAvailableActions();
  }

  selectBulkAction(id: string): void {
    this.adminBulkActionService.bulkAction(this.selectedEntries, id).subscribe(() => {
      this.search();
    });
  }
}

