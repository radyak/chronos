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
import {AdminEntryComponent} from "./views/admin-entry/admin-entry.component";
import {AdminConfirmService} from "./services/admin-confirm.service";
import {EntriesTableComponent} from "../../ui-components/entries-table/entries-table.component";
import {AdminBulkActionService} from "./services/admin-bulk-action.service";
import {AddTagsBulkActionService} from "./services/bulk-actions/add-tags-bulk-action.service";
import {AdminCopyEntryComponent} from "./views/admin-copy-entry/admin-copy-entry.component";
import {AdminNewEntryComponent} from "./views/admin-new-entry/admin-new-entry.component";
import {AdminRelationTypesComponent} from "./views/admin-relation-types/admin-relation-types.component";
import {AdminRelationTypesService} from "./services/admin-relation-types.service";
import {TooltipComponent} from "../../ui-components/tooltip/tooltip.component";
import {
  AdminEntriesSelectInputComponent
} from "./components/admin-entries-select-input/admin-entries-select-input.component";
import {AdminEditRelationModal} from "./components/admin-edit-relation-modal/admin-edit-relation-modal";
import {
  AdminRelationTypeSelectInputComponent
} from "./components/admin-relation-type-select-input/admin-relation-type-select-input.component";


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
        path: 'entries/new',
        component: AdminNewEntryComponent
      },
      {
        path: 'entries/:id',
        component: AdminEntryComponent
      },
      {
        path: 'entries/:id/copy',
        component: AdminCopyEntryComponent
      },
      {
        path: 'tags',
        component: AdminTagsComponent
      },
      {
        path: 'relation-types',
        component: AdminRelationTypesComponent
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
    AdminEntryComponent,
    AdminNewEntryComponent,
    AdminCopyEntryComponent,
    AdminRelationTypesComponent,
    AdminComponent,
    AdminEntriesSelectInputComponent,
    AdminRelationTypeSelectInputComponent,
    AdminEditRelationModal
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
    TagSelectionComponent,
    EntriesTableComponent,
    TooltipComponent,
  ],
  providers: [
    AdminTagCategoriesService,
    AdminTagsService,
    AdminEntriesService,
    AdminConfirmService,
    AdminBulkActionService,
    AddTagsBulkActionService,
    AdminRelationTypesService,
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
