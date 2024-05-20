import {Component, ElementRef, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {faClose} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";


@Component({
  standalone: true,
  selector: 'chronos-year-range-input',
  templateUrl: './year-range-input.component.html',
  styleUrls: ['./year-range-input.component.scss'],
  imports: [
    CommonModule,
    NgbModule,
    FormsModule,
    FontAwesomeModule
  ]
})
export class YearRangeInputComponent {

  resetIcon = faClose;
  invalid = false;

  @ViewChild('fromInput') fromInput?: ElementRef;

  @Output()
  public submit: EventEmitter<void> = new EventEmitter<void>();

  private _from = 0;
  @Input()
  set from(from: number) {
    this._from = from;
    this.validate();
    this.fromChange.emit(this._from);
  }
  get from() {
    return this._from;
  }
  @Output()
  fromChange: EventEmitter<number> = new EventEmitter<number>();

  private _to = 0;
  @Input()
  set to(to: number) {
    this._to = to;
    this.validate();
    this.toChange.emit(this._to);
  }
  get to() {
    return this._to;
  }
  @Output()
  toChange: EventEmitter<number> = new EventEmitter<number>();

  protected doFocus() {
    this.fromInput?.nativeElement.focus();
  }

  protected keyDown(event: any) {
    if (event.key === 'Enter' && !this.invalid) {
      this.submit.emit();
    }
  }

  public resetDateRange(): void {
    this.from = 0;
    this.to = 0;
  }

  private validate(): void {
    this.invalid = this.from > this.to;
  }
}
