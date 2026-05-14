import { computed, inject, Injectable, ResourceRef, signal, Signal } from '@angular/core';
import { AdminConfirmService } from './admin-confirm.service';
import { SchemaClient } from './schema.client';
import { catchError, firstValueFrom, from, map, Observable, of, tap } from 'rxjs';
import { TypeAO } from 'src/app/common/model/domain/schema/admin/type.ao';
import { TypeMapper } from 'src/app/common/model/domain/schema/mappers/type.mapper';
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
  public readonly defaultTypeAttributes = computed(() => this.schema()?.types.defaultAttributes?.map(AttributeMapper.dtoToAo) ?? []);
  public readonly defaultRelationAttributes = computed(() => this.schema()?.relations.defaultAttributes?.map(AttributeMapper.dtoToAo) ?? []);

  public allTypes(reloadTrigger: Signal<number> = signal(0)): ResourceRef<TypeAO[] | undefined> {
    return rxResource({
      params: () => reloadTrigger?.(),
      stream: () => this.schemaClient.getSchema().pipe(
          map((schemaDto: SchemaResponseDTO) =>
            (schemaDto?.types.elements ?? []).map(type => TypeMapper.dtoToAo(type, schemaDto))
          )
        )
    });
  }

  public schemaTypeResource(typeIdentifier: string): ResourceRef<TypeAO | undefined> {
    return rxResource({
      stream: () => {
        if (!typeIdentifier) {
          return of({
            defaultAttributes: this.defaultTypeAttributes(),
          } as TypeAO);
        }
        return this.schemaClient.getType(typeIdentifier).pipe(
          map(TypeMapper.fromSchemaResponseDTO)
        );
      },
    });
  }

  public saveType(type: TypeAO): Observable<void> {
    return this.schemaClient.saveType(type).pipe(
      catchError((err: any, caught: Observable<void>) => {
        this.notificationService.error(`Error while saving type "${type.key}"`);
        throw new Error("Type not saved");
      }),
      tap(() => {
        this.notificationService.success(`Type "${type.key}" saved successfully`);
      })
    );
  }

  public deleteType(type: TypeAO): Observable<void> {
    return from(
      this.confirmService.confirm(
        `Confirm Delete ${type.key}`,
        `Do you want to delete schema type ${type?.key}?`
      ).then(
        () =>
          firstValueFrom(this.schemaClient.deleteType(type)).then(
            () => {
              this.notificationService.success(`Type "${type.key}" deleted successfully`);
            },
            (err) => {
              this.notificationService.error(`Error while deleting type "${type.key}"`);
              throw new Error("Type not deleted");
            }
          ),
        () => {
          // Nothing todo
        }
      )
    );
  }

}
