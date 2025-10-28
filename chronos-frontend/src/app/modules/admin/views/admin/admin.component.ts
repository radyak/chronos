import {Component} from '@angular/core';
import { RouterModule } from '@angular/router';
import { NotificationsComponent } from 'src/app/ui-components/notifications/notifications.component';

@Component({
  selector: 'chronos-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
  standalone: true,
  imports: [
    RouterModule,
    NotificationsComponent
  ]
})
export class AdminComponent {
}
