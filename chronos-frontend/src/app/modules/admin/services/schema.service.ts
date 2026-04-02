import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Resource, resource, ResourceRef, Signal, signal } from '@angular/core';
import { firstValueFrom, map, of } from 'rxjs';
import { EntityAO } from 'src/app/common/model/domain/schema/admin/entity.ao';
import { EntityDTO } from 'src/app/common/model/domain/schema/entity.dto';
import { EntityMapper } from 'src/app/common/model/domain/schema/mappers/entity.mapper';
import { SchemaResponseDTO } from 'src/app/common/model/domain/schema/schema-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchemaService {
  private http = inject(HttpClient);
  private apiUrl = '/api/schema';
  
  public schemaResource(/*reloadTrigger: Signal<unknown>*/): ResourceRef<SchemaResponseDTO | undefined> {
    return resource({
      loader: (param) => {
        return firstValueFrom(this.http.get<SchemaResponseDTO>(this.apiUrl));
      },
    });
  }

  public schemaEntityResource(entityIdentifier: Signal<string>): ResourceRef<EntityAO | undefined> {
    return resource({
      loader: async () => {
        if (!entityIdentifier()) {
          return firstValueFrom(of({}))
        }
        return await firstValueFrom(
          this.http.get<SchemaResponseDTO>(`${this.apiUrl}/${entityIdentifier()}`).pipe(
            map(EntityMapper.fromSchemaResponseDTO)
          )
        );
      },
    });
  }
}
