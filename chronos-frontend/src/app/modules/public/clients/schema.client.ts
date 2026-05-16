import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { TypeAO } from 'src/app/common/model/schema/admin/type.ao';
import { SchemaResponseDTO } from 'src/app/common/model/schema/schema-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchemaClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/schema';
  
  public getSchema(): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(this.apiUrl);
  }

  public getType(typeIdentifier: string): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(`${this.apiUrl}/${typeIdentifier}`);
  }

}
