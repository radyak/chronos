import { computed, inject, Injectable, ResourceRef, signal, Signal } from '@angular/core';
import { AdminConfirmService } from './admin-confirm.service';
import { SchemaClient } from './schema.client';
import { catchError, firstValueFrom, from, map, Observable, of, tap } from 'rxjs';
import { EntityAO } from 'src/app/common/model/domain/schema/admin/entity.ao';
import { EntityMapper } from 'src/app/common/model/domain/schema/mappers/entity.mapper';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { AttributeMapper } from 'src/app/common/model/domain/schema/mappers/attribute.mapper';
import { SchemaResponseDTO } from 'src/app/common/model/domain/schema/schema-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchemaService {

  // Injected Dependencies
  private schemaClient = inject(SchemaClient);
  private confirmService = inject(AdminConfirmService);
  private notificationService = inject(NotificationService);

  // "Cache"
  public readonly schema = toSignal(this.schemaClient.getSchema());
  public readonly defaultEntityAttributes = computed(() => this.schema()?.entities.defaultAttributes?.map(AttributeMapper.dtoToAo) ?? []);
  public readonly defaultRelationAttributes = computed(() => this.schema()?.relations.defaultAttributes?.map(AttributeMapper.dtoToAo) ?? []);
  
  public allEntities(reloadTrigger: Signal<number> = signal(0)): ResourceRef<EntityAO[] | undefined> {
    return rxResource({
      params: () => reloadTrigger?.(),
      stream: () => this.schemaClient.getSchema().pipe(
          map((schemaDto: SchemaResponseDTO) => 
            (schemaDto?.entities.elements ?? []).map(entity => EntityMapper.dtoToAo(entity, schemaDto))
          )
        )
    });
  }

  public schemaEntityResource(entityIdentifier: string): ResourceRef<EntityAO | undefined> {
    return rxResource({
      stream: () => {
        if (!entityIdentifier) {
          return of({
            defaultAttributes: this.defaultEntityAttributes(),
          } as EntityAO);
        }
        return this.schemaClient.getEntity(entityIdentifier).pipe(
          map(EntityMapper.fromSchemaResponseDTO)
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
