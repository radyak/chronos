import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Route, RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {NgxColorsModule} from "ngx-colors";
import {NgbDropdownModule} from "@ng-bootstrap/ng-bootstrap";
import {DateInputComponent} from "../../ui-components/date-input/date-input.component";
import {TagComponent} from "../../ui-components/tag/tag.component";
import {ThemeShowcaseViewComponent} from "./views/theme-showcase-view/theme-showcase-view.component";


const routes: Route[] = [
  {
    path: 'showcase',
    component: ThemeShowcaseViewComponent
  },
]

@NgModule({
  declarations: [
    ThemeShowcaseViewComponent
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
    TagComponent
  ],
  providers: [
  ]
})
export class DevModule { }
