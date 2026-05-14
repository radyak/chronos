import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { TypeAO } from 'src/app/common/model/domain/schema/admin/type.ao';
import { SchemaResponseDTO } from 'src/app/common/model/domain/schema/schema-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchemaClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/schema';
  private adminApiUrl = '/api/schema/admin/types';
  
  
  public getSchema(): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(this.apiUrl);
  }

  public getType(typeIdentifier: string): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(`${this.apiUrl}/${typeIdentifier}`);
  }

  public saveType(type: TypeAO): Observable<void> {
    if (type.id) {
      return this.http.put<void>(`${this.adminApiUrl}/${type.key}`, type).pipe(take(1));
    } else {
      return this.http.post<void>(`${this.adminApiUrl}`, type).pipe(take(1));
    }
  }

  public deleteType(type: TypeAO): Observable<void> {
    if (type.key) {
      return this.http.delete<void>(`${this.adminApiUrl}/${type.key}`).pipe(take(1));
    }
    return of()
  }
}
