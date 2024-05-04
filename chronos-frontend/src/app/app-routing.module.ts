import {inject, isDevMode, NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthService} from "./security/auth.service";

const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./modules/public/public.module').then((m) => m.PublicModule),
  },
  {
    path: 'admin',
    canActivate: [
      () => inject(AuthService).assertLoggedIn()
    ],
    loadChildren: () =>
      import('./modules/admin/admin.module').then((m) => m.AdminModule),
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
