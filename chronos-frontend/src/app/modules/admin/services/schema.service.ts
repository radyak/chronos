import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource, ResourceRef, Signal } from '@angular/core';
import { AdminConfirmService } from './admin-confirm.service';
import { SchemaClient } from './schema.client';
import { SchemaResponseDTO } from 'src/app/common/model/domain/schema/schema-response.dto';
import { catchError, firstValueFrom, from, map, Observable, of, tap } from 'rxjs';
import { EntityAO } from 'src/app/common/model/domain/schema/admin/entity.ao';
import { EntityMapper } from 'src/app/common/model/domain/schema/mappers/entity.mapper';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root',
})
export class SchemaService {
  private schemaClient = inject(SchemaClient);
  private confirmService = inject(AdminConfirmService);
  private notificationService = inject(NotificationService);
  
  public schemaResource(reloadTrigger: Signal<number>): ResourceRef<SchemaResponseDTO | undefined> {
    return resource({
      params: () => reloadTrigger(),
      loader: async (param) => {
        return await firstValueFrom(this.schemaClient.getSchema());
      },
    });
  }

  public schemaEntityResource(entityIdentifier: string): ResourceRef<EntityAO | undefined> {
    return resource({
      loader: async () => {
        if (!entityIdentifier) {
          return firstValueFrom(of({}))
        }
        return await firstValueFrom(
          this.schemaClient.getEntity(entityIdentifier).pipe(
            map(EntityMapper.fromSchemaResponseDTO)
          )
        );
      },
    });
  }

  public saveEntity(entity: EntityAO): Observable<void> {
    return this.schemaClient.saveEntity(entity).pipe(
      catchError((err: any, caught: Observable<void>) => {
        this.notificationService.error(`Error while saving entity "${entity.key}"`);
        throw new Error("Entity not saved");
      }),
      tap(() => {
        this.notificationService.success(`Entity "${entity.key}" saved successfully`);
      })
    );
  }

  public deleteEntity(entity: EntityAO): Observable<void> {
    return from(
      this.confirmService.confirm(
        `Confirm Delete ${entity.key}`,
        `Do you want to delete schema entity ${entity?.key}?`
      ).then(
        () => 
          firstValueFrom(this.schemaClient.deleteEntity(entity)).then(
            () => {
              this.notificationService.success(`Entity "${entity.key}" deleted successfully`);
            },
            (err) => {
              this.notificationService.error(`Error while deleting entity "${entity.key}"`);
              throw new Error("Entitiy not deleted");
            }
          ),
        () => {
          // Nothing todo
        }
      )
    );
  }

}
