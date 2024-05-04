import {Injectable, Optional} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {OAuthModuleConfig, OAuthResourceServerErrorHandler, OAuthStorage} from "angular-oauth2-oidc";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authStorage: OAuthStorage,
    private errorHandler: OAuthResourceServerErrorHandler,
    @Optional() private moduleConfig: OAuthModuleConfig
  ) {
  }

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const url = req.url.toLowerCase();
    const found = this.moduleConfig?.resourceServer?.allowedUrls?.find(u => url.indexOf(u) !== -1);
    if (!found) return next.handle(req);

    const sendAccessToken = this.moduleConfig?.resourceServer?.sendAccessToken;

    if (sendAccessToken) {

      let token = this.authStorage.getItem('access_token');
      let header = 'Bearer ' + token;

      let headers = req.headers
        .set('Authorization', header);

      req = req.clone({ headers });
    }

    return next.handle(req).pipe(catchError(err => this.errorHandler.handleError(err)));

  }
}
