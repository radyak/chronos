import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse, HttpResponse
} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {NotificationService} from "../../../ui-components/notifications/notification.service";

interface ErrorConfig {
  title: string;
  message: string;
}

const errorMap: Map<number, ErrorConfig> = new Map([
  [400, { title: "Client error", message: "Invalid data sent to server"}],
  [404, { title: "Client error", message: "Resource not found"}],
  [500, { title: "Server error", message: "Unknown error occurred on server side"}],
]);

@Injectable()
export class AdminErrorInterceptor implements HttpInterceptor {

  constructor(private notificationService: NotificationService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      tap({
        next: (event: HttpEvent<any>) => {},
        error: (error: HttpErrorResponse) => {
          const errorConfig = errorMap.get(error.status)
          if (errorConfig) {
            this.notificationService.error(errorConfig.message, errorConfig.title)
          }
        }
      })
    );
  }
}
