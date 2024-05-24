import {Component} from '@angular/core';
import {AdminEntryComponent} from "../admin-entry/admin-entry.component";


@Component({
  selector: 'chronos-admin-copy-entry',
  templateUrl: '../admin-entry/admin-entry.component.html',
  styleUrls: ['../admin-entry/admin-entry.component.scss']
})
export class AdminCopyEntryComponent extends AdminEntryComponent {

  override afterLoadEntry(): void {
    delete this.currentEntry?.id;
  }

  override pageTitle(): string {
    return `Copy ${this.currentEntry?.title}`;
  }

  override breadCrumbTitle(): string {
    return `Copy ${this.currentEntry?.title}`;;
  }

}

