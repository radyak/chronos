import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BreadcrumbsComponent } from "src/app/common/components/breadcrumbs/breadcrumbs.component";

@Component({
    selector: 'chronos-admin',
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.scss'],
    imports: [
        RouterOutlet,
        BreadcrumbsComponent
    ]
})
export class AdminComponent {
}
