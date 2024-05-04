import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AdminOverviewComponent} from './views/admin-overview/admin-overview.component';
import {AdminTagsComponent} from './views/admin-tags/admin-tags.component';
import {Route, RouterModule} from "@angular/router";
import {AdminTagCategoriesService} from "./services/admin-tag-categories.service";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {AdminErrorInterceptor} from "./interceptors/admin-error.interceptor";
import {
  TagCategoriesSectionComponent
} from './views/admin-tags/tag-categories-section/tag-categories-section.component';
import {TagsSectionComponent} from "./views/admin-tags/tags-section/tags-section.component";
import {AdminTagsService} from "./services/admin-tags.service";
import {NgxColorsModule} from "ngx-colors";
import {NgbDropdownModule} from "@ng-bootstrap/ng-bootstrap";
import {AdminEntriesComponent} from './views/admin-entries/admin-entries.component';
import {AdminEntriesService} from "./services/admin-entries.service";
import {DateInputComponent} from "../../ui-components/date-input/date-input.component";
import {TagComponent} from "../../ui-components/tag/tag.component";
import {AdminComponent} from './views/admin/admin.component';
import {NavbarComponent} from "../../ui-components/navbar/navbar.component";
import {FooterComponent} from "../../ui-components/footer/footer.component";
import {NotificationsComponent} from "../../ui-components/notifications/notifications.component";
import {WikipediaSummaryComponent} from "../../ui-components/wikipedia-summary/wikipedia-summary.component";
import {TagSelectionComponent} from "../../ui-components/tag-selection/tag-selection.component";
import {AuthInterceptor} from "./interceptors/auth.interceptor";


const routes: Route[] = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: '',
        component: AdminOverviewComponent
      },
      {
        path: 'entries',
        component: AdminEntriesComponent
      },
      {
        path: 'tags',
        component: AdminTagsComponent
      }
    ]
  }
]

@NgModule({
  declarations: [
    AdminOverviewComponent,
    AdminTagsComponent,
    TagCategoriesSectionComponent,
    TagsSectionComponent,
    AdminEntriesComponent,
    AdminComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    HttpClientModule,
    FormsModule,
    FontAwesomeModule,
    NgbDropdownModule,
    NgxColorsModule,
    DateInputComponent,
    TagComponent,
    NavbarComponent,
    FooterComponent,
    NotificationsComponent,
    WikipediaSummaryComponent,
    TagSelectionComponent
  ],
  providers: [
    AdminTagCategoriesService,
    AdminTagsService,
    AdminEntriesService,
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
  ]
})
export class AdminModule { }
