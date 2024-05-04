import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PublicTimelineComponent} from './views/public-timeline/public-timeline.component';
import {Route, RouterModule} from "@angular/router";
import {PublicComponent} from './views/public/public.component';
import {FooterComponent} from "../../ui-components/footer/footer.component";
import {NavbarComponent} from "../../ui-components/navbar/navbar.component";
import {NotificationsComponent} from "../../ui-components/notifications/notifications.component";
import {PublicOverviewComponent} from "./views/public-overview/public-overview.component";
import {TagsService} from "./service/tags.service";
import {EntriesService} from "./service/entries.service";
import {HttpClientModule} from "@angular/common/http";
import {
  NgbDropdown,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle,
  NgbTypeaheadModule
} from "@ng-bootstrap/ng-bootstrap";
import {TagComponent} from "../../ui-components/tag/tag.component";
import {FormsModule} from "@angular/forms";
import {TimelineComponent} from "../../ui-components/timeline/timeline.component";
import {YearRangeInputComponent} from "../../ui-components/year-range-input/year-range-input.component";
import {WikipediaSummaryComponent} from "../../ui-components/wikipedia-summary/wikipedia-summary.component";
import {TagCategoriesService} from "./service/tag-categories.service";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {TagSelectionComponent} from "../../ui-components/tag-selection/tag-selection.component";
import {PublicDiscoverComponent} from './views/public-discover/public-discover.component';
import {
    EntriesSearchInputComponent
} from "../../functional-components/entries-search-input/entries-search-input.component";


const routes: Route[] = [
  {
    path: '',
    component: PublicComponent,
    children: [
      {
        path: '',
        component: PublicOverviewComponent
      },
      {
        path: 'discover',
        component: PublicDiscoverComponent
      },
      {
        path: 'timeline',
        component: PublicTimelineComponent
      }
    ]
  }
]

@NgModule({
  declarations: [
    PublicComponent,
    PublicOverviewComponent,
    PublicTimelineComponent,
    PublicDiscoverComponent
  ],
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forChild(routes),
        FooterComponent,
        NavbarComponent,
        NotificationsComponent,
        NgbTypeaheadModule,
        TagComponent,
        FormsModule,
        TimelineComponent,
        YearRangeInputComponent,
        WikipediaSummaryComponent,
        NgbDropdown,
        NgbDropdownItem,
        NgbDropdownMenu,
        NgbDropdownToggle,
        FontAwesomeModule,
        TagSelectionComponent,
        EntriesSearchInputComponent
    ],
  providers: [
    TagsService,
    EntriesService,
    TagCategoriesService
  ]
})
export class PublicModule { }
