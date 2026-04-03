import {Component} from '@angular/core';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCircleNodes, faDatabase } from '@fortawesome/free-solid-svg-icons';

@Component({
    selector: 'chronos-admin',
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.scss'],
    imports: [
        RouterModule,
        FontAwesomeModule
    ]
})
export class AdminComponent {
    protected schemaIcon = faCircleNodes;
    protected dataIcon = faDatabase;
}
