import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors
} from '@angular/forms';
import { catchError, first, map, Observable, of, switchMap, tap, timer } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class UniquenessValidatorService {

  private readonly http = inject(HttpClient);

  /**
   * Validates that the control value does NOT already exist against the backend's API.
   */
  public apiUniqueValidator(attribute: string, elementId?: string | null): AsyncValidatorFn {

    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value: string = (control.value ?? '').toString().trim();

      // Skip the HTTP call for empty / unchanged values
      if (!value || value.length < 3) {
        return of(null);
      }

      return this.http.get<boolean>(
        `/api/data/admin/unique?key=${attribute}&value=${value}&elementId=${elementId ?? null}`
      ).pipe(
          map((res) => {
            if (res === false) {
              return {
                notUnique: {
                  value: value,
                  message: `"${value}" already exists for attribute ${attribute}`,
                },
              }
            }
            return null;
          }),
          catchError(() =>
            // Treat network / server errors as a validation pass so a temporary
            // outage does not block the user from submitting the form.
            of(null)
          ),
          // Complete after the first emission so Angular marks the control
          // status as VALID / INVALID rather than leaving it as PENDING.
          first()
        );

    };
  }

}