import {inject, isDevMode, NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthService} from "./security/auth.service";
import { adminRoutes } from './modules/admin/admin.routes';
import { PublicOverviewComponent } from './modules/public/public-overview/public-overview.component';

const routes: Routes = [
  {
    path: '',
    component: PublicOverviewComponent
  },
  {
    path: 'admin',
    canActivate: [
      () => inject(AuthService).assertLoggedIn()
    ],
    children: adminRoutes,
    // loadChildren: () =>
    //   import('./modules/admin/admin.module').then((m) => m.AdminModule),
  }
];

if(isDevMode()) {
  routes.push(
    {
      path: 'dev',
      loadChildren: () =>
        import('./modules/dev/dev.module').then((m) => m.DevModule),
    }
  )
}

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
