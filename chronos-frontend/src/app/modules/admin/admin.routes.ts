import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { Route } from "@angular/router";
import { AdminErrorInterceptor } from "./interceptors/admin-error.interceptor";
import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { AdminConfirmService } from "./services/admin-confirm.service";
import { AdminCopyPersonComponent } from "./views/admin-copy-person/admin-copy-person.component";
import { AdminNewPersonComponent } from "./views/admin-new-person/admin-new-person.component";
import { AdminOverviewComponent } from './views/admin-overview/admin-overview.component';
import { AdminPersonComponent } from "./views/admin-person/admin-person.component";
import { AdminPersonsComponent } from './views/admin-persons/admin-persons.component';
import { AdminComponent } from './views/admin/admin.component';


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
