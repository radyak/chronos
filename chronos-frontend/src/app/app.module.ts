import {APP_INITIALIZER, inject, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {NavbarComponent} from "./ui-components/navbar/navbar.component";
import {FooterComponent} from "./ui-components/footer/footer.component";
import {OAuthModule, OAuthService} from "angular-oauth2-oidc";
import {AuthService} from "./security/auth.service";
import {HttpClientModule} from "@angular/common/http";
import {WebAppConfigService} from "./service/web-app-config.service";
import {AuthServiceMock} from "./security/auth.service.mock";
import {environment} from "../environments/environment";

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    NavbarComponent,
    FooterComponent,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['/api/admin'],
        sendAccessToken: true
      },
    })
  ],
  providers: [
    {
      provide: AuthService,
      useFactory: () => {
        if (environment.mockAuth) {
          return new AuthServiceMock();
        }
        return new AuthService(inject(OAuthService), inject(WebAppConfigService));
      }
    },
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [WebAppConfigService],
      useFactory: (webAppConfigService: WebAppConfigService) => {
        return () => {
          //Make sure to return a promise!
          return webAppConfigService.loadAppConfig();
        };
      }
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
