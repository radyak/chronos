import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {Observable, of} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Router} from "@angular/router";
import {AdminConfirmService} from "../../services/admin-confirm.service";
import {calculateMaxSpanningDateRange} from "../../../../util/date-range.utils";
import {DateRange} from "../../../../model/date-range.model";


@Component({
  selector: 'chronos-admin-entries',
  templateUrl: './admin-entries.component.html',
  styleUrls: ['./admin-entries.component.scss']
})
export class AdminEntriesComponent implements OnInit {

  entries$: Observable<Array<Entry>> = of([]);
  searchEntryTitle: string = '';

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;
  deleteIcon = faTrash;

  @ViewChild('content', {static: false})
  modalContent!: TemplateRef<void>;

  constructor(private adminEntriesService: AdminEntriesService,
              private router: Router,
              private route: ActivatedRoute,
              private confirmService: AdminConfirmService) {
  }

  ngOnInit(): void {
    this.entries$ = this.adminEntriesService.allEntries();
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
      this.adminEntriesService.deleteEntry(entry).subscribe(() => {
        this.entries$ = this.adminEntriesService.allEntries();
      })
    });
  }

  filterEntry(entry: Entry): boolean {
    return !!entry.title && entry.title.toLowerCase().includes(this.searchEntryTitle.toLowerCase())
  }

  entryDateSpan(entry: Entry): DateRange {
    return calculateMaxSpanningDateRange(entry.dateRanges);
  }

}

