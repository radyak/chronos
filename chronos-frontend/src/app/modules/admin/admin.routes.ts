import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { Route } from "@angular/router";
import { AdminErrorInterceptor } from "./interceptors/admin-error.interceptor";
import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { AdminConfirmService } from "./services/admin-confirm.service";
import { AdminCopyPersonComponent } from "./views/admin/data/admin-copy-person/admin-copy-person.component";
import { AdminNewPersonComponent } from "./views/admin/data/admin-new-person/admin-new-person.component";
import { AdminPersonComponent } from "./views/admin/data/admin-person/admin-person.component";
import { AdminPersonsComponent } from './views/admin/data/admin-persons/admin-persons.component';
import { AdminComponent } from './views/admin/admin.component';
import { SchemaComponent } from "./views/admin/schema/schema.component";
import { EditEntityComponent } from "./views/admin/schema/edit-entity/edit-entity.component";
import { AdminOverviewComponent } from "./views/admin/overview/admin-overview/admin-overview.component";

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
