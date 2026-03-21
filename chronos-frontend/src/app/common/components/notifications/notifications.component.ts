import {Component} from '@angular/core';

import {NgbToastModule} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "./notification.service";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";

@Component({
    selector: 'chronos-notifications',
    styleUrls: ['./notifications.component.scss'],
    templateUrl: './notifications.component.html',
    imports: [NgbToastModule, FontAwesomeModule],
    host: {
        class: 'toast-container position-fixed top-0 end-0 p-3',
        style: 'z-index: 1200'
    }
})
export class NotificationsComponent {

  constructor(protected notificationService: NotificationService) {
  }
}
