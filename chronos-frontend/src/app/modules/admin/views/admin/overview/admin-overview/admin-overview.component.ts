import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCircleNodes, faDatabase } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'chronos-admin-overview',
  imports: [
    FontAwesomeModule,
    RouterLink
],
  templateUrl: './admin-overview.component.html',
  styleUrl: './admin-overview.component.scss',
})
export class AdminOverviewComponent {
    protected schemaIcon = faCircleNodes;
    protected dataIcon = faDatabase;
}
