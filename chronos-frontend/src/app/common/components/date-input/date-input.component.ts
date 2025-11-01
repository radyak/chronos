import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import { paddedString } from 'src/app/common/util/padded-string.function';

const months = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

interface EditDate {
  year: number,
  month?: number,
  day?: number
}

@Component({
  standalone: true,
  selector: 'chronos-date-input',
  templateUrl: './date-input.component.html',
  styleUrls: ['./date-input.component.scss'],
  imports: [
    CommonModule,
    NgbModule,
    FormsModule
  ]
})
export class DateInputComponent {

  private _editDate: EditDate = {
    year: 0,
    month: 1,
    day: 1
  };
  get editDate() {
    return this._editDate
  }
  set editDate(editDate: EditDate) {
    this._editDate = editDate;
    this.update();
  }

  @Input()
  set date(date: string | undefined) {
    this._editDate = this.fromDateString(date);
  }

  @Output()
  dateChange: EventEmitter<string> = new EventEmitter<string>();

  update() {
    this.dateChange.emit(this.toDateString(this._editDate))
  }

  private toDateString(editDate?: EditDate): string | undefined {
    if (!editDate) {
      return undefined;
    }
    const sign = editDate.year < 0 ? '-00' : ''
    return `${sign}${paddedString(Math.abs(editDate.year), 4)}-${paddedString(editDate.month, 2, true)}-${paddedString(editDate.day, 2, true)}`
  }

  private fromDateString(dateString?: string): EditDate {
    if (!dateString) {
      dateString = "";
    }
    const dateComponents = dateString.split('-');
    let yearSing = 1;
    if (dateComponents.length === 4) {
      dateComponents.shift();
      yearSing = -1
    }
    return {
      year: yearSing * parseInt(dateComponents[0]) || 0,
      month: parseInt(dateComponents[1]) || 0,
      day: parseInt(dateComponents[2]) || 0,
    }
  }

  monthName(monthNumber?: number): string {
    return monthNumber ? months[monthNumber] : '';
  }

}
