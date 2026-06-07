import {Component, EventEmitter, forwardRef, input, Input, Output, signal} from '@angular/core';

import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from "@angular/forms";
import { paddedString } from 'src/app/common/util/padded-string.function';

const months = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

interface EditDate {
  year: number,
  month?: number,
  day?: number
}

@Component({
    selector: 'chronos-date-input',
    templateUrl: './date-input.component.html',
    styleUrls: ['./date-input.component.scss'],
    imports: [
      NgbModule,
      FormsModule
    ],
    providers: [
      {
        provide: NG_VALUE_ACCESSOR,
        useExisting: forwardRef(() => DateInputComponent),
        multi: true
      }
    ]
})
export class DateInputComponent implements ControlValueAccessor {

  // Properties
  public disabled = input(false);
  private formControlOnChange: Function = () => {};
  private formControlOnTouched: Function = () => {};

  // Smart Properties
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

  // Methods
  writeValue(obj: any): void {
    this.date = obj;
  }

  registerOnChange(fn: Function): void {
    this.formControlOnChange = fn;
  }

  registerOnTouched(fn: Function): void {
    this.formControlOnTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // this.disabled.set(isDisabled);
  }

  protected update() {
    const value = this.toDateString(this._editDate);
    this.dateChange.emit(value);
    this.formControlOnChange(value);
    this.formControlOnTouched(value);
  }

  protected monthName(monthNumber?: number): string {
    return monthNumber ? months[monthNumber] : '';
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

}
