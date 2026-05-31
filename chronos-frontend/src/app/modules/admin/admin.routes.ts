import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { Route } from "@angular/router";
import { AdminErrorInterceptor } from "./interceptors/admin-error.interceptor";
import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { AdminConfirmService } from "./services/admin-confirm.service";
import { AdminComponent } from './views/admin.component';
import { SchemaComponent } from "./views/schema/schema.component";
import { EditTypeComponent as EditTypeComponent } from "./views/schema/edit-type/edit-type.component";
import { AdminOverviewComponent } from "./views/overview/admin-overview/admin-overview.component";
import { DataOverviewComponent } from "./views/data/data-overview/data-overview.component";
import { EditEntryComponent } from "./views/data/edit-entry/edit-entry.component";

export const CREATE_ROUTE_KEYWORD = 'new'

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
    data: {
      breadCrumb: 'Administration'
    },
    children: [
      {
        path: '',
        component: AdminOverviewComponent,
      },
      {
        path: 'schema',
        data: {
          breadCrumb: 'Schema'
        },
        children: [
          {
            path: '',
            component: SchemaComponent,
          },
          {
            path: ':id',
            component: EditTypeComponent,
            data: {
              breadCrumb: 'Edit Type'
            }
          },
        ]
      },
      {
        path: 'data',
        data: {
          breadCrumb: 'Data'
        },
        children: [
          {
            path: '',
            component: DataOverviewComponent,
          },
          {
            path: ':id',
            component: EditEntryComponent,
            data: {
              breadCrumb: 'Edit Entry'
            }
          },
        ]
      },
    ]
  },
]
