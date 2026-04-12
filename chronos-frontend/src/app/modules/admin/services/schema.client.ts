import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Resource, resource, ResourceRef, Signal, signal } from '@angular/core';
import { firstValueFrom, map, Observable, of, take } from 'rxjs';
import { EntityAO } from 'src/app/common/model/domain/schema/admin/entity.ao';
import { EntityDTO } from 'src/app/common/model/domain/schema/entity.dto';
import { EntityMapper } from 'src/app/common/model/domain/schema/mappers/entity.mapper';
import { SchemaResponseDTO } from 'src/app/common/model/domain/schema/schema-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchemaClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/schema';
  private adminApiUrl = '/api/schema/admin/entities';
  
  
  public getSchema(): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(this.apiUrl);
  }

  public getEntity(entityIdentifier: string): Observable<SchemaResponseDTO> {
    return this.http.get<SchemaResponseDTO>(`${this.apiUrl}/${entityIdentifier}`);
  }

  public saveEntity(entity: EntityAO): Observable<void> {
    if (entity.id) {
      return this.http.put<void>(`${this.adminApiUrl}/${entity.key}`, entity).pipe(take(1));
    } else {
      return this.http.post<void>(`${this.adminApiUrl}`, entity).pipe(take(1));
    }
  }

  public deleteEntity(entity: EntityAO): Observable<void> {
    if (entity.key) {
      return this.http.delete<void>(`${this.adminApiUrl}/${entity.key}`).pipe(take(1));
    }
    return of()
  }
}
