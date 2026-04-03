import {Component, inject} from '@angular/core';

import { BreadcrumbsService } from './breadcrumbs.service';
import { RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Breadcrumb } from './breadcrumb.model';
import { startWith } from 'rxjs';

@Component({
    selector: 'chronos-breadcrumbs',
    styleUrls: ['./breadcrumbs.component.scss'],
    templateUrl: './breadcrumbs.component.html',
    imports: [AsyncPipe, RouterLink],
})
export class BreadcrumbsComponent {
    protected readonly breadcrumbs$ = inject(BreadcrumbsService).breadcrumbs$;
}
