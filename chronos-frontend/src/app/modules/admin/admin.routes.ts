import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AdminOverviewComponent} from './views/admin-overview/admin-overview.component';
import {Route, RouterModule} from "@angular/router";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {AdminErrorInterceptor} from "./interceptors/admin-error.interceptor";
import {NgxColorsModule} from "ngx-colors";
import {NgbDropdownModule} from "@ng-bootstrap/ng-bootstrap";
import {AdminPersonsComponent} from './views/admin-persons/admin-persons.component';
import {DateInputComponent} from "../../ui-components/date-input/date-input.component";
import {AdminComponent} from './views/admin/admin.component';
import {NavbarComponent} from "../../ui-components/navbar/navbar.component";
import {FooterComponent} from "../../ui-components/footer/footer.component";
import {NotificationsComponent} from "../../ui-components/notifications/notifications.component";
import {WikipediaSummaryComponent} from "../../ui-components/wikipedia-summary/wikipedia-summary.component";
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import {AdminPersonComponent} from "./views/admin-person/admin-person.component";
import {AdminConfirmService} from "./services/admin-confirm.service";
import {EntitiesTableComponent} from "../../ui-components/entities-table/entities-table.component";
import {AdminCopyPersonComponent} from "./views/admin-copy-person/admin-copy-person.component";
import {AdminNewPersonComponent} from "./views/admin-new-person/admin-new-person.component";


export const adminRoutes: Route[] = [
  {
    path: '',
    component: AdminComponent,
    providers: [
      AdminConfirmService,
      {
        provide: HTTP_INTERCEPTORS,
        useClass: AdminErrorInterceptor,
        multi: true
      },
      {
        provide: HTTP_INTERCEPTORS,
        useClass: AuthInterceptor,
        multi: true
      }
    ],
    children: [
      {
        path: '',
        component: AdminOverviewComponent,
      },
      {
        path: 'persons',
        component: AdminPersonsComponent,
      },
      {
        path: 'persons/new',
        component: AdminNewPersonComponent
      },
      {
        path: 'persons/:id',
        component: AdminPersonComponent
      },
      {
        path: 'persons/:id/copy',
        component: AdminCopyPersonComponent
      },
    ]
  }
]
