import { Component, computed, effect, inject, Signal, signal, TemplateRef, WritableSignal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { firstValueFrom, map } from 'rxjs';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { CREATE_ROUTE_KEYWORD } from 'src/app/modules/admin/admin.routes';
import { FormBuilder, FormGroup, FormsModule, NgModel, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditEntityFormMapper } from './edit-entity-form.mapper';
import { NgbAccordionModule, NgbDropdownModule, NgbModal, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';
import { EditEntityAttributeDialogComponent } from './edit-entity-attribute-dialog/edit-entity-attribute-dialog.component';
import { SchemaService } from 'src/app/modules/admin/services/schema.service';
import { IconsService } from 'src/app/modules/admin/services/icons.service';
import { uniqueValidator } from 'src/app/common/util/unique-validator';
import { FormService } from 'src/app/common/util/form.service';
import { AdminIconsService } from 'src/app/modules/admin/services/admin-icons.service';
import { RelationAO } from 'src/app/common/model/domain/schema/admin/relation.ao';
import { EditEntityRelationFormComponent } from './edit-entity-relation-form/edit-entity-relation-form.component';

@Component({
  selector: 'chronos-edit-entity',
  imports: [
    RouterModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    NgbAccordionModule,
    NgbDropdownModule,
    FormsModule
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
  private modalService = inject(NgbModal);
  private adminIconsService = inject(AdminIconsService);
  private formService = inject(FormService);
	private offcanvasService = inject(NgbOffcanvas);

  // Icons
  protected saveIcon = IconsService.ICON_SAVE;
  protected cancelIcon = IconsService.ICON_CANCEL;
  protected editIcon = IconsService.ICON_EDIT;
  protected deleteIcon = IconsService.ICON_DELETE;
  protected iconNames = computed(() => {
    const search = this.iconSearch().toLocaleLowerCase();
    return this.adminIconsService.iconNames.filter(icon =>
      icon.toLowerCase().includes(search)
    )
  });

  // Derived Data Fields
  protected entityId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected entityResource = this.schemaService.schemaEntityResource(this.entityId());
  protected schemaResource = this.schemaService.schemaResource();
  protected takenEntityNames: Signal<(string)[]> = computed(() => {
    const entity = this.entityResource.value();
    return this.schemaResource.value()?.entities.elements
          .filter(el => !!el && el !== entity && el.id !== entity?.id && el.key)
          .map(el => el.key as string)
           ?? []
  });
  protected isNew = computed(() => !this.entityResource.value()?.id);

  // Form & controls
  protected form?: FormGroup;
  protected currentAttribute: WritableSignal<AttributeAO | undefined> = signal(undefined);
  protected iconSearch = signal("");
  protected submitted = false;

  // Init
  constructor() {
    effect(() => {
      const entity = this.entityResource.value();
      if (!entity) {
        return;
      }
      this.form = this.fb.group(
        {
          key: [null,
            [
              Validators.required,
              Validators.minLength(3),
              Validators.maxLength(64),
              uniqueValidator(this.takenEntityNames)
            ],
          ],
          explanation: [null, 
            [
              Validators.minLength(3),
              Validators.maxLength(255)
            ]
          ],
          examples: [null, 
            [
              Validators.minLength(3),
              Validators.maxLength(255)
            ]
          ],
        },
      );

      this.form.patchValue(entity); // partial safe update
    });
  }

  protected editAttribute(attr: AttributeAO): void {
    const modalRef = this.modalService.open(EditEntityAttributeDialogComponent);
    modalRef.componentInstance.attribute.set(attr);
    const takenAttributeNames = this.entityResource.value()?.attributes?.filter(a => a !== attr).map(a => a.key);
    modalRef.componentInstance.attribute.set(attr);
    modalRef.componentInstance.takenAttributeNames.set(takenAttributeNames);

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

  protected addNewAttribute(): void {
    this.editAttribute({});
  }

  protected editRelation(rel: RelationAO): void {
		const offcanvasRef = this.offcanvasService.open(EditEntityRelationFormComponent, {
      position: 'bottom',
      backdrop: 'static'
    });
		offcanvasRef.componentInstance.relation = rel;

    offcanvasRef.result.then(resultRelation => {
      if (!resultRelation) {
        return;
      }
      const entity = this.entityResource.value();
      if (!entity) {
        return;
      }
      entity.relations = entity.relations ?? [];
      let index = entity?.relations?.indexOf(rel);
      if (index === -1) {
        index = entity.relations.length;
      }
      entity.relations[index] = resultRelation;
    },
    (err) => {
      // Nothing to do
    })
  }

  protected addNewRelation(): void {
    this.editRelation({});
  }

  // Methods
  protected save(): void {
    this.submitted = true;
    const entity = EditEntityFormMapper.toAO(this.form?.getRawValue(), this.entityResource.value());
    firstValueFrom(this.schemaService.saveEntity(entity)).then(
      () => {
        this.back();
      },
      (err) => {
        // Nothing todo
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

  protected delete(): void {
    const entity = this.entityResource.value();
    if (!entity) {
      return;
    }
    firstValueFrom(this.schemaService.deleteEntity(entity)).then(
      () => {
        this.back();
      },
      (err) => {
        // Nothing todo
      }
    );
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

  protected deleteRelation(relation: RelationAO): void {
    this.adminConfirmService.confirm(
      `Delete Relation`,
      `Do you want to delete relation '${relation.key} (-> ${relation.target?.key})'?`
    ).then(
      () => {
        const entity = this.entityResource.value();
        let index = entity?.relations?.indexOf(relation);
        if (index === undefined || index === -1) {
          return;
        }
        entity?.relations?.splice(index, 1);
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected back(): void {
    this.router.navigate(['..'], { relativeTo: this.route })
  }

  protected selectIcon(icon: string): void {
    const entity = this.entityResource.value();
    if (!entity) {
      return;
    }
    entity.icon = icon;
    this.iconSearch.set("");
  }

  protected isInvalid(field: string): boolean {
    const ctrl = this.form?.get(field);
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched));
  }

  protected errors(field: string, label: string): string[] {
    return this.formService.extractErrors(field, label, this.form);
  }

}
