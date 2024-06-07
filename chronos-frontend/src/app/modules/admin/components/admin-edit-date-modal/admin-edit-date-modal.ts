import {Component, inject, Input} from '@angular/core';
import {
  NgbActiveModal,
  NgbDropdown,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle
} from "@ng-bootstrap/ng-bootstrap";
import {NgForOf, NgIf} from "@angular/common";
import {DateRange, DateRangeType} from "../../../../model/date-range.model";
import {DateInputComponent} from "../../../../ui-components/date-input/date-input.component";


@Component({
  standalone: true,
  selector: 'chronos-admin-edit-date-modal',
  templateUrl: './admin-edit-date-modal.html',
  imports: [
    NgIf,
    DateInputComponent,
    NgForOf,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle
  ]
})
export class AdminEditDateModal {

  activeModal = inject(NgbActiveModal);

  @Input()
  set dateRange(dateRange: DateRange) {
    this.originalRange = dateRange;
    this.editedRange = {
      ...dateRange
    };
  }

  originalRange!: DateRange;
  editedRange!: DateRange;

  allDateRangeTypes(): Array<DateRangeType> {
    return Object.values(DateRangeType);
  }

}

