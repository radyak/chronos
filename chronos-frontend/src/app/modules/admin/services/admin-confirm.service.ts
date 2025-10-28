import {Injectable} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AdminConfirmModal} from "../components/admin-confirm-modal/admin-confirm-modal";

@Injectable({
  providedIn: 'root'
})
export class AdminConfirmService {

  constructor(private modal: NgbModal) {
  }

  public confirm(title: string, message: string): Promise<any> {
    const modalRef = this.modal.open(AdminConfirmModal, { ariaLabelledBy: 'modal-basic-title' })
    modalRef.componentInstance.title = title;
    modalRef.componentInstance.message = message;
    return modalRef.result
  }

}
