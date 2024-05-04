import {Injectable} from '@angular/core';
import {OAuthService} from "angular-oauth2-oidc";
import {filter, map, Observable, ReplaySubject, Subject} from "rxjs";
import {UserProfile} from "./user-profile.interface";
import {WebAppConfigService} from "../service/web-app-config.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _profile$: Subject<UserProfile | undefined> = new ReplaySubject(undefined);

  get profile$(): Observable<UserProfile> {
    return this._profile$.asObservable().pipe(
      filter(profile => !!profile),
      map(profile => profile as UserProfile)
    );
  }

  constructor(private oAuthService: OAuthService,
              private webAppConfigService: WebAppConfigService) {
    this.oAuthService.configure({
      ...this.webAppConfigService.config,
      //issuer: 'http://localhost:8020/realms/radshift',
      //clientId: 'chronos-admin-ui',
      redirectUri: window.location.origin,
      responseType: 'code',
      strictDiscoveryDocumentValidation: false,
      scope: 'openid profile email offline_access',
      // showDebugInformation: true
    });
    this.oAuthService.setupAutomaticSilentRefresh();
    this.oAuthService.loadDiscoveryDocumentAndTryLogin();
    this.oAuthService.events.subscribe(e => {
      const claims = this.oAuthService.getIdentityClaims()
      if (!claims) {
        return
      }
      this._profile$.next({
          name: claims["name"]
      });
    });
  }

  assertLoggedIn(): boolean {
    if(!this.oAuthService.hasValidAccessToken()) {
      this.login();
      return false;
    }
    return true;
  }

  isLoggedIn(): boolean {
    return this.oAuthService.hasValidAccessToken();
  }

  login(): void {
    this.oAuthService.initCodeFlow();
  }

  logout(): void {
    this.oAuthService.logOut();
  }
}
