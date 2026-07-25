import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { SchemaTypeAO } from 'src/app/common/model/schema/admin/type.ao';

@Injectable({
  providedIn: 'root',
})
export class AdminSchemaClient {
  private http = inject(HttpClient);
  private adminApiUrl = '/api/schema/admin/types';
  
  public saveType(type: SchemaTypeAO): Observable<void> {
    if (type.id) {
      return this.http.put<void>(`${this.adminApiUrl}/${type.key}`, type).pipe(take(1));
    } else {
      return this.http.post<void>(`${this.adminApiUrl}`, type).pipe(take(1));
    }
  }

  public deleteType(type: SchemaTypeAO): Observable<void> {
    if (type.key) {
      return this.http.delete<void>(`${this.adminApiUrl}/${type.key}`).pipe(take(1));
    }
    return of()
  }
}
