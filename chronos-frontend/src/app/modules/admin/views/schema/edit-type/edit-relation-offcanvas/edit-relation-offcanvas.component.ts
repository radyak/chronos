import { Component, effect, inject, model, ModelSignal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveOffcanvas, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SchemaAttributeAO } from 'src/app/common/model/schema/admin/attribute.ao';
import { SchemaRelationAO } from 'src/app/common/model/schema/admin/relation.ao';
import { FormService } from 'src/app/common/util/form.service';
import { uniqueValidator } from 'src/app/common/validators/unique-validator';
import { IconConstants as IconsConfig } from 'src/app/common/constants/icon.constants';
import { EditAttributeDialogComponent } from '../edit-attribute-dialog/edit-attribute-dialog.component';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { TypeSelectComponent } from '../type-select/type-select.component';
import { BackendErrorService } from 'src/app/common/util/backend-error.service';
import { ApiErrorDTO } from 'src/app/common/model/error-response.dto';

@Component({
  selector: 'chronos-edit-relation-offcanvas',
  imports: [
    FontAwesomeModule,
    ReactiveFormsModule,
    TypeSelectComponent
],
  templateUrl: './edit-relation-offcanvas.component.html',
  styleUrl: './edit-relation-offcanvas.component.scss',
})
export class EditRelationOffcanvasComponent {
  // Dependencies
	protected offcanvas = inject(NgbActiveOffcanvas);
  private fb = inject(FormBuilder);
  private formService = inject(FormService);
  private backendErrorService = inject(BackendErrorService);
  private modalService = inject(NgbModal);
  private adminConfirmService = inject(AdminConfirmService);

  // Inputs
  protected relation: ModelSignal<SchemaRelationAO> = model({});
  protected takenKeys: ModelSignal<string[]> = model([] as string[]);
  protected backendErrors: ModelSignal<ApiErrorDTO[]> = model<ApiErrorDTO[]>([]);

  // Icons
  protected saveIcon = IconsConfig.ICON_SAVE;
  protected cancelIcon = IconsConfig.ICON_CANCEL;
  protected editIcon = IconsConfig.ICON_EDIT;
  protected deleteIcon = IconsConfig.ICON_DELETE;
  protected warnIcon = IconsConfig.ICON_WARNING;

  // Form & controls
  protected submitted = false;
  protected form: FormGroup = this.fb.group(
    {
      key: [null,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64),
          uniqueValidator(this.takenKeys)
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
      target: [null, 
        [
          Validators.required
        ]
      ],
    },
  );

  // Init
  constructor() {
    effect(() => {
      this.form.patchValue(this.relation()); // partial safe update
    });
  }

  // Methods
  protected isInvalid(field: string): boolean {
    const ctrl = this.form?.get(field);
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched)) || this.hasBackendError(field);
  }

  protected errors(field: string, label: string): string[] {
    return [
      ...this.formService.extractErrors(field, label, this.form),
      ...this.backendErrorService.extractErrors(field, label, this.backendErrors())
    ];
  }

  protected confirm(): void {
    this.submitted = true;
    const relation: SchemaRelationAO = {
      ...this.relation(),
      ...this.form?.getRawValue(),      
    };
    this.offcanvas.close(relation);
  }

  protected cancel(): void {
    this.offcanvas.dismiss();
  }

  protected addNewAttribute(): void {
    this.editAttribute({});
  }

  protected editAttribute(attr: SchemaAttributeAO): void {
    const modalRef = this.modalService.open(EditAttributeDialogComponent);
    modalRef.componentInstance.attribute.set(attr);
    const takenAttributeNames = this.relation()?.attributes?.filter(a => a !== attr).map(a => a.key);
    modalRef.componentInstance.takenAttributeNames.set(takenAttributeNames);
    const backendErrors = this.getBackendErrorsSection(`attributes[${this.relation()?.attributes?.indexOf(attr)}]`);
    modalRef.componentInstance.backendErrors.set(backendErrors);

    modalRef.result.then(resultAttribute => {
      if (!resultAttribute) {
        return;
      }
      const relation = this.relation();
      relation.attributes = relation.attributes ?? [];
      let index = relation?.attributes?.indexOf(attr);
      if (index === -1) {
        index = relation.attributes.length;
      }
      relation.attributes[index] = resultAttribute;
    },
    (err) => {
      // Nothing to do
    })
  }

  protected deleteAttribute(attribute: SchemaAttributeAO): void {
    this.adminConfirmService.confirm(
      `Delete Attribute`,
      `Do you want to delete attribute '${attribute.key}'?`
    ).then(
      () => {
        const relation = this.relation();
        let index = relation?.attributes?.indexOf(attribute);
        if (index === undefined || index === -1) {
          return;
        }
        relation?.attributes?.splice(index, 1);
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected hasBackendError(field: string): boolean {
    return this.backendErrors()?.some(e => e.field === field);
  }

  protected hasBackendErrorMatching(path: string): boolean {
    return this.backendErrors().some(e => e.field.startsWith(path));
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
