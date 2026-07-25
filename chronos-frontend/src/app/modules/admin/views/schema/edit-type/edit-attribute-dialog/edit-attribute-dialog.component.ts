import { Component, computed, effect, inject, model, ModelSignal, signal, WritableSignal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ApiErrorDTO } from 'src/app/common/model/error-response.dto';
import { SchemaAttributeAO } from 'src/app/common/model/schema/admin/attribute.ao';
import { SchemaAttributeTypeDTO } from 'src/app/common/model/schema/attribute-type.dto';
import { BackendErrorService } from 'src/app/common/util/backend-error.service';
import { FormService } from 'src/app/common/util/form.service';
import { uniqueValidator } from 'src/app/common/validators/unique-validator';

@Component({
  selector: 'chronos-edit-attribute-dialog',
  imports: [
    ReactiveFormsModule,
    FontAwesomeModule,
    FormsModule
  ],
  templateUrl: './edit-attribute-dialog.component.html',
  styleUrl: './edit-attribute-dialog.component.scss',
})
export class EditAttributeDialogComponent {

  // Dependencies
  private fb = inject(FormBuilder);
  private activeModal = inject(NgbActiveModal);
  private formService = inject(FormService);
  private backendErrorService = inject(BackendErrorService);

  // Inputs
  protected attribute: ModelSignal<SchemaAttributeAO | undefined> = model();
  protected takenAttributeNames: ModelSignal<string[] | undefined> = model();
  protected backendErrors: ModelSignal<ApiErrorDTO[]> = model<ApiErrorDTO[]>([]);

  // Icons
  protected deleteIcon = faTrash;
  protected addIcon = faPlus;

  // Form & controls
  protected form!: FormGroup;
  protected submitted = false;
  protected types: SchemaAttributeTypeDTO[] = Object.values(SchemaAttributeTypeDTO)
      .filter(t => typeof t !== "number")
      .map(t => t as unknown as SchemaAttributeTypeDTO)
      .filter(t => t !== SchemaAttributeTypeDTO.WIKIQID);
  protected newAllowedValue: WritableSignal<string> = signal("");

  // Constants
  protected readonly STRING = SchemaAttributeTypeDTO.STRING;
  protected readonly NUMBER = SchemaAttributeTypeDTO.NUMBER;
  protected readonly ENUM = SchemaAttributeTypeDTO.ENUM;
  protected readonly DATENOTATION = SchemaAttributeTypeDTO.DATENOTATION;
  protected readonly WIKIQID = SchemaAttributeTypeDTO.WIKIQID;

  // Init
  constructor() {
    effect(() => {
      console.log("Backend errors in edit attribute dialog changed:", this.backendErrors());
    });
    this.form = this.fb.group({
      key: [null,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64),
          uniqueValidator(this.takenAttributeNames)
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
      type: [null, 
        [
          Validators.required
        ]
      ],
      isMandatory: [null],
      isUnique: [null],
      isChangeable: [null],
      isArray: [null],
      order: [null],
      valuePattern: [
        null, 
        [
          Validators.minLength(3),
          Validators.maxLength(255)
        ]
      ],
      valueRange: [
        null, 
        [
          Validators.minLength(3),
          Validators.maxLength(255)
        ]
      ],
    });

    effect(() => {
      const attribute = this.attribute();
      if (!attribute) {
        return;
      }
      this.form.patchValue(attribute);
    });
  }

  protected confirm(): void {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const attributeResult = this.form.getRawValue();
    attributeResult.allowedValues = this.attribute()?.allowedValues;
    this.activeModal.close(attributeResult);
  }

  protected cancel(): void {
    this.activeModal.dismiss()
  }

  protected isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched)) || this.hasBackendError(field);
  }

  protected errors(field: string, label: string): string[] {
    return [
      ...this.formService.extractErrors(field, label, this.form),
      ...this.backendErrorService.extractErrors(field, label, this.backendErrors())
    ];
  }

  protected isNew = computed(() => !this.attribute()?.id);

  protected deleteAllowedValue(allowedValue: string): void {
    let index = this.attribute()?.allowedValues?.indexOf(allowedValue);
    if (index === undefined || index === -1) {
      return;
    }
    this.attribute()?.allowedValues?.splice(index, 1);
  }

  protected addAllowedValue(): void {
    const newAllowedValue = this.newAllowedValue();
    const attribute = this.attribute();
    if (!newAllowedValue || !attribute || attribute?.allowedValues?.find(a => a === newAllowedValue)) {
      return;
    }
    attribute!.allowedValues = this.attribute()!.allowedValues ?? [];
    attribute!.allowedValues?.push(newAllowedValue!);
    this.newAllowedValue.set("");
  }

  protected isType(type: SchemaAttributeTypeDTO): boolean {
    return this.form.get("type")?.value === type;
  }

  protected hasBackendError(field: string): boolean {
    return this.backendErrors()?.some(e => e.field === field);
  }

}
