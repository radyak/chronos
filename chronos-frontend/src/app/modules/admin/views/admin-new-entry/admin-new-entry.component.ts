import {Component} from '@angular/core';
import {AdminEntryComponent} from "../admin-entry/admin-entry.component";


@Component({
  selector: 'chronos-admin-new-entry',
  templateUrl: '../admin-entry/admin-entry.component.html',
  styleUrls: ['../admin-entry/admin-entry.component.scss']
})
export class AdminNewEntryComponent extends AdminEntryComponent {

  override setCurrentEntry(): void {
    this.currentEntry = {
      title: "",
      tags: [],
      dateRanges: []
    };
  }

  override afterLoadEntry(): void {
    delete this.currentEntry?.id;
  }

  override pageTitle(): string {
    return `New Entry`;
  }

  override breadCrumbTitle(): string {
    return `New Entry`;
  }

}

