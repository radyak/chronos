import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Entry} from "../../../../model/entry.model";
import {debounceTime, distinctUntilChanged, filter, Observable, of, Subject} from "rxjs";
import {AdminEntriesService} from "../../services/admin-entries.service";

@Component({
  selector: 'chronos-admin-entries-select-input',
  templateUrl: './admin-entries-select-input.component.html',
  styleUrls: ['./admin-entries-select-input.component.scss']
})
export class AdminEntriesSelectInputComponent implements OnInit {

  @Input()
  entry?: Entry;

  @Output()
  entryChange: EventEmitter<Entry> = new EventEmitter<Entry>();

  entries$: Observable<Array<Entry>> = of();

  inputValue = new Subject<string>();
  trigger = this.inputValue.pipe(
    debounceTime(300),
    filter((v: string) => (!!v && v.length > 2)),
    distinctUntilChanged()
  );

  constructor(private adminEntriesService: AdminEntriesService) {

  }

  ngOnInit() {
    this.trigger.subscribe(currentValue => {
      this.entries$ = this.adminEntriesService.allEntries({
        title: currentValue
      })
    });
  }

  search(): void {
  }

  select(entry: Entry): void {
    this.entry = entry;
    this.entryChange.emit(entry);
  }

  protected onType(event: any) {
    this.inputValue.next(event.target.value);
  }

}
