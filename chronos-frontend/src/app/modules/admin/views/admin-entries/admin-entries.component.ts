import {Component, OnInit} from '@angular/core';
import {AdminEntriesService} from "../../services/admin-entries.service";
import {Observable, of} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faPenToSquare, faPlus, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Router} from "@angular/router";


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

  constructor(private adminEntriesService: AdminEntriesService,
              private router: Router,
              private route: ActivatedRoute) {
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
    this.adminEntriesService.deleteEntry(entry).subscribe(() => {
      this.entries$ = this.adminEntriesService.allEntries();
    })
  }

  filterEntry(entry: Entry): boolean {
    return !!entry.title && entry.title.toLowerCase().includes(this.searchEntryTitle.toLowerCase())
  }

}

