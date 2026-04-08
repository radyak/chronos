import { Component, computed, effect, inject, signal, WritableSignal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { faPen, faSave, faTrash, faXmark } from '@fortawesome/free-solid-svg-icons';
import { SchemaService } from 'src/app/modules/admin/services/schema.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { firstValueFrom, map } from 'rxjs';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { CREATE_ROUTE_KEYWORD } from 'src/app/modules/admin/admin.routes';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { EditEntityFormMapper } from './edit-entity-form.mapper';
import { NgbAccordionModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';
import { EditEntityAttributeDialogComponent } from './edit-entity-attribute-dialog/edit-entity-attribute-dialog.component';

@Component({
  selector: 'chronos-edit-entity',
  imports: [
    RouterModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    NgbAccordionModule
],
  templateUrl: './edit-entity.component.html',
  styleUrl: './edit-entity.component.scss',
})
export class EditEntityComponent {

  // Dependencies
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private adminConfirmService = inject(AdminConfirmService);
  private schemaService = inject(SchemaService);
  private fb = inject(FormBuilder);
  private notificationService = inject(NotificationService);
  private modalService = inject(NgbModal);

  // Icons
  protected saveIcon = faSave;
  protected cancelIcon = faXmark;
  protected editIcon = faPen;
  protected deleteIcon = faTrash;

  // Derived Data Fields
  protected entityId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected entityResource = this.schemaService.schemaEntityResource(this.entityId);
  protected isNew = computed(() => !this.entityResource.value()?.id);

  // Form & controls
  protected form!: FormGroup;
  protected currentAttribute: WritableSignal<AttributeAO | undefined> = signal(undefined);

  // Init
  constructor() {
    effect(() => {
      const entity = this.entityResource.value();
      if (!entity) {
        return;
      }
      this.form = this.fb.group(
        {
          key: [
            null,
            [
              Validators.required,
              Validators.minLength(3),
              Validators.maxLength(64)
            ],
            // [this.usernameTakenValidator()]
          ],
          explanation: [
            null, 
            [
              Validators.minLength(3),
              Validators.maxLength(255)
            ]
          ],
          examples: [
            null, 
            [
              Validators.minLength(3),
              Validators.maxLength(255)
            ]
          ],
        },
        // { validators: this.passwordMatchValidator }
      );

      this.form.patchValue(entity); // partial safe update
    });
  }

  protected editAttribute(attr: AttributeAO): void {
    const modalRef = this.modalService.open(EditEntityAttributeDialogComponent);
    modalRef.componentInstance.attribute.set(attr);

    modalRef.result.then(resultAttribute => {
      if (!resultAttribute) {
        return;
      }
      const entity = this.entityResource.value();
      if (!entity) {
        return;
      }
      entity.attributes = entity.attributes ?? [];
      let index = entity?.attributes?.indexOf(attr);
      if (index === -1) {
        index = entity.attributes.length;
      }
      entity.attributes[index] = resultAttribute;
    },
    (err) => {
      // Nothing to do
    })

    if (this.currentAttribute() == attr) {
      this.currentAttribute.set(undefined);
    } else {
      this.currentAttribute.set(attr);
    }
  }

  protected editNewAttribute(): void {
    this.editAttribute({});
  }

  // Methods
  protected save(): void {
    const entity = EditEntityFormMapper.toAO(this.form.getRawValue(), this.entityResource.value());
    console.log("Saving entity:", entity);
    firstValueFrom(this.schemaService.saveEntity(entity)).then(
      () => {
        this.notificationService.success(`Entity "${entity.key}" saved successfully`);
        this.back();
      },
      (err) => {
        this.notificationService.error(`Error while saving entity "${entity.key}"`);
        console.log("Error saving", err)
      }
    );
  }

  protected cancel(): void {
    this.adminConfirmService.confirm(
      'Confirm Cancel Edit',
      'Do you want to cancel editing this schema entity and leave without saving?'
    ).then(
      () => {
        this.back()
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected deleteAttribute(attribute: AttributeAO): void {
    this.adminConfirmService.confirm(
      `Delete Attribute`,
      `Do you want to delete attribute '${attribute.key}'?`
    ).then(
      () => {
        const entity = this.entityResource.value();
        let index = entity?.attributes?.indexOf(attribute);
        if (index === undefined || index === -1) {
          return;
        }
        entity?.attributes?.splice(index, 1);
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected back(): void {
    this.router.navigate(['..'], { relativeTo: this.route })
  }

}
