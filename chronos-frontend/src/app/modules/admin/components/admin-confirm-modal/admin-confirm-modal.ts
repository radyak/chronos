import {Component, inject, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NgIf} from "@angular/common";


@Component({
  standalone: true,
  selector: 'chronos-admin-confirm-modal',
  templateUrl: './admin-confirm-modal.html',
  imports: [
    NgIf
  ],
  styleUrls: ['./admin-confirm-modal.scss']
})
export class AdminConfirmModal {

  activeModal = inject(NgbActiveModal);

  @Input()
  title!: string;

  @Input()
  message!: string;

  @Input()
  cancelLabel?: string;

  @Input()
  confirmLabel?: string = 'Ok';

}

