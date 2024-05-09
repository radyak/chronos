import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, MonoTypeOperatorFunction, Observable, of, tap} from "rxjs";
import {Entry} from "../../../model/entry.model";
import {NotificationService} from "../../../ui-components/notifications/notification.service";
import {WikipediaSummary} from "../../../model/wikipedia-summary.model";
import {AdminEntriesService} from "./admin-entries.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AdminConfirmModal} from "../components/admin-confirm-modal/admin-confirm-modal";

@Injectable({
  providedIn: null
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
