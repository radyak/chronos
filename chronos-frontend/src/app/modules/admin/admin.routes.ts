import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { Route } from "@angular/router";
import { AdminErrorInterceptor } from "./interceptors/admin-error.interceptor";
import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { AdminConfirmService } from "./services/admin-confirm.service";
import { AdminComponent } from './views/admin.component';
import { SchemaComponent } from "./views/schema/schema.component";
import { EditEntityComponent } from "./views/schema/edit-entity/edit-entity.component";
import { AdminOverviewComponent } from "./views/overview/admin-overview/admin-overview.component";
import { DataOverviewComponent } from "./views/data/data-overview/data-overview.component";

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
            component: EditEntityComponent,
            data: {
              breadCrumb: 'Edit Entity'
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
        ]
      },
      // {
      //   path: 'data',
      //   component: AdminPersonsComponent,
      //   data: {
      //     breadCrumb: 'Data'
      //   }
      // },
      // {
      //   path: 'data/persons',
      //   component: AdminPersonsComponent,
      // },
      // {
      //   path: 'data/persons/new',
      //   component: AdminNewPersonComponent
      // },
      // {
      //   path: 'data/persons/:id',
      //   component: AdminPersonComponent
      // },
      // {
      //   path: 'data/persons/:id/copy',
      //   component: AdminCopyPersonComponent
      // },
    ]
  },
]
