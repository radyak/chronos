import {NgModule} from '@angular/core';
import {Route, RouterModule} from "@angular/router";
import {ThemeShowcaseViewComponent} from "./views/theme-showcase-view/theme-showcase-view.component";


const routes: Route[] = [
  {
    path: 'showcase',
    component: ThemeShowcaseViewComponent
  },
]

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
})
export class DevModule { }
