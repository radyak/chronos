import { Component, computed, effect, inject, Signal, signal, WritableSignal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { firstValueFrom, map } from 'rxjs';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { CREATE_ROUTE_KEYWORD } from 'src/app/modules/admin/admin.routes';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditTypeFormMapper } from './edit-type-form.mapper';
import { NgbAccordionModule, NgbModal, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SchemaAttributeAO } from 'src/app/common/model/schema/admin/attribute.ao';
import { EditAttributeDialogComponent } from './edit-attribute-dialog/edit-attribute-dialog.component';
import { AdminSchemaService } from 'src/app/modules/admin/services/admin-schema.service';
import { IconConstants } from 'src/app/common/constants/icon.constants';
import { uniqueValidator } from 'src/app/common/validators/unique-validator';
import { FormService } from 'src/app/common/util/form.service';
import { SchemaRelationAO } from 'src/app/common/model/schema/admin/relation.ao';
import { EditRelationOffcanvasComponent } from './edit-relation-offcanvas/edit-relation-offcanvas.component';
import { IconSelectComponent } from "./icon-select/icon-select.component";
import { ApiErrorDTO, ErrorResponseDTO } from 'src/app/common/model/error-response.dto';
import { BackendErrorService } from 'src/app/common/util/backend-error.service';

@Component({
  selector: 'chronos-edit-type',
  imports: [
    RouterModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    NgbAccordionModule,
    IconSelectComponent
],
  templateUrl: './edit-type.component.html',
  styleUrl: './edit-type.component.scss',
})
export class EditTypeComponent {

  // Dependencies
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private adminConfirmService = inject(AdminConfirmService);
  private schemaService = inject(AdminSchemaService);
  private fb = inject(FormBuilder);
  private modalService = inject(NgbModal);
  private formService = inject(FormService);
  private backendErrorService = inject(BackendErrorService);
	private offcanvasService = inject(NgbOffcanvas);

  // Icons
  protected saveIcon = IconConstants.ICON_SAVE;
  protected cancelIcon = IconConstants.ICON_CANCEL;
  protected editIcon = IconConstants.ICON_EDIT;
  protected deleteIcon = IconConstants.ICON_DELETE;
  protected helpIcon = IconConstants.ICON_HELP;
  protected warnIcon = IconConstants.ICON_WARNING;

  // Derived Data Fields
  protected typeId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected typeResource = this.schemaService.schemaTypeResource(this.typeId());
  protected allTypes = this.schemaService.allTypes();
  protected takenTypeNames: Signal<string[]> = computed(() => {
    const type = this.typeResource.value();
    return (this.allTypes.value() ?? [])
          .filter(el => !!el && el !== type && el.id !== type?.id && el.key)
          .map(el => el.key as string)
           ?? []
  });
  protected isNew = computed(() => !this.typeResource.value()?.id);
  protected backendErrors: WritableSignal<ApiErrorDTO[]> = signal([]);

  // Form & controls
  protected form?: FormGroup;
  protected currentAttribute: WritableSignal<SchemaAttributeAO | undefined> = signal(undefined);
  protected submitted = false;
  protected forceDisplayErrors = false;

  // Init
  constructor() {
    effect(() => {
      const type = this.typeResource.value();
      if (!type) {
        return;
      }
      this.form = this.fb.group(
        {
          key: [null,
            [
              Validators.required,
              Validators.minLength(3),
              Validators.maxLength(64),
              uniqueValidator(this.takenTypeNames)
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
          icon: [null, 
            [
              Validators.required
            ]
          ],
        },
      );

      this.form.patchValue(type); // partial safe update
    });
  }

  protected editAttribute(attr: SchemaAttributeAO): void {
    const modalRef = this.modalService.open(EditAttributeDialogComponent);
    modalRef.componentInstance.attribute.set(attr);
    const takenAttributeNames = this.typeResource.value()?.attributes?.filter(a => a !== attr).map(a => a.key);
    modalRef.componentInstance.takenAttributeNames.set(takenAttributeNames);
    const backendErrors = this.getBackendErrorsSection(`attributes[${this.typeResource.value()?.attributes?.indexOf(attr)}]`);
    modalRef.componentInstance.backendErrors.set(backendErrors);

    modalRef.result.then(resultAttribute => {
      if (!resultAttribute) {
        return;
      }
      const type = this.typeResource.value();
      if (!type) {
        return;
      }
      type.attributes = type.attributes ?? [];
      let index = type?.attributes?.indexOf(attr);
      if (index === -1) {
        index = type.attributes.length;
      }
      type.attributes[index] = resultAttribute;
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

  protected editRelation(rel: SchemaRelationAO): void {
		const offcanvasRef = this.offcanvasService.open(EditRelationOffcanvasComponent, {
      position: 'bottom',
      backdrop: 'static'
    });
    const takenKeys = this.typeResource.value()?.relations?.filter(a => a !== rel).map(a => a.key);
		offcanvasRef.componentInstance.relation.set(rel);
		offcanvasRef.componentInstance.takenKeys.set(takenKeys);
    const backendErrors = this.getBackendErrorsSection(`relations[${this.typeResource.value()?.relations?.indexOf(rel)}]`);
    offcanvasRef.componentInstance.backendErrors.set(backendErrors);

    offcanvasRef.result.then(resultRelation => {
      if (!resultRelation) {
        return;
      }
      const type = this.typeResource.value();
      if (!type) {
        return;
      }
      type.relations = type.relations ?? [];
      let index = type?.relations?.indexOf(rel);
      if (index === -1) {
        index = type.relations.length;
      }
      type.relations[index] = resultRelation;
    },
    (err) => {
      // Nothing to do
    })
  }

  protected addNewRelation(): void {
    this.editRelation({
      defaultAttributes: this.schemaService.defaultRelationAttributes()
    });
  }

  // Methods
  protected save(returnAfterSave: boolean = false): void {
    this.submitted = true;
    const type = EditTypeFormMapper.toAO(this.form?.getRawValue(), this.typeResource.value());
    firstValueFrom(this.schemaService.saveType(type)).then(
      () => {
        if (returnAfterSave) {
          this.back();
        }
      },
      (err: ErrorResponseDTO) => {
        this.backendErrors.set(err.error?.errors ?? []);
      }
    );
  }

  protected cancel(): void {
    this.adminConfirmService.confirm(
      'Confirm Cancel Edit',
      'Do you want to cancel editing this schema type and leave without saving?'
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
    const type = this.typeResource.value();
    if (!type) {
      return;
    }
    firstValueFrom(this.schemaService.deleteType(type)).then(
      () => {
        this.back();
      },
      (err) => {
        // Nothing todo
      }
    );
  }

  protected deleteAttribute(attribute: SchemaAttributeAO): void {
    this.adminConfirmService.confirm(
      `Delete Attribute`,
      `Do you want to delete attribute '${attribute.key}'?`
    ).then(
      () => {
        const type = this.typeResource.value();
        let index = type?.attributes?.indexOf(attribute);
        if (index === undefined || index === -1) {
          return;
        }
        type?.attributes?.splice(index, 1);
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected deleteRelation(relation: SchemaRelationAO): void {
    this.adminConfirmService.confirm(
      `Delete Relation`,
      `Do you want to delete relation '${relation.key} (-> ${relation.target?.key})'?`
    ).then(
      () => {
        const type = this.typeResource.value();
        let index = type?.relations?.indexOf(relation);
        if (index === undefined || index === -1) {
          return;
        }
        type?.relations?.splice(index, 1);
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected back(): void {
    this.router.navigate(['..'], { relativeTo: this.route })
  }

  protected isInvalid(field: string): boolean {
    const ctrl = this.form?.get(field);
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched || this.forceDisplayErrors)) || this.hasBackendError(field);
  }

  protected errors(field: string, label: string): string[] {
    return [
      ...this.formService.extractErrors(field, label, this.form),
      ...this.backendErrorService.extractErrors(field, label, this.backendErrors())
    ];
  }

  protected hasBackendError(field: string): boolean {
    return this.backendErrors().some(e => e.field === field);
  }

  protected hasBackendErrorMatching(path: string): boolean {
    return this.backendErrors().some(e => e.field.startsWith(path));
  }

  protected toggleForceDisplayErrors(): void {
    this.forceDisplayErrors = !this.forceDisplayErrors;
  }

  private getBackendErrorsSection(fieldPathPrefix: string): ApiErrorDTO[] {
    return this.backendErrors()
      .filter(e => e.field.startsWith(fieldPathPrefix))
      .map(e => ({
        ...e,
        field: e.field.replace(`${fieldPathPrefix}.`, '')
      }));
  }

}
