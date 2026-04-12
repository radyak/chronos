import { Component, computed, effect, inject, model, ModelSignal, signal, WritableSignal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';
import { AttributeTypeDTO } from 'src/app/common/model/domain/schema/attribute-type.dto';
import { FormService } from 'src/app/common/util/form.service';
import { uniqueValidator } from 'src/app/common/util/unique-validator';

@Component({
  selector: 'chronos-edit-entity-attribute-dialog',
  imports: [
    ReactiveFormsModule,
    FontAwesomeModule,
    FormsModule
  ],
  templateUrl: './edit-entity-attribute-dialog.component.html',
  styleUrl: './edit-entity-attribute-dialog.component.scss',
})
export class EditEntityAttributeDialogComponent {

  // Dependencies
  private fb = inject(FormBuilder);
  private activeModal = inject(NgbActiveModal);
  private formService = inject(FormService);

  // Inputs
  protected attribute: ModelSignal<AttributeAO | undefined> = model();
  protected takenAttributeNames: ModelSignal<string[] | undefined> = model();

  // Icons
  protected deleteIcon = faTrash;
  protected addIcon = faPlus;

  // Form & controls
  protected form!: FormGroup;
  protected submitted = false;
  protected types: AttributeTypeDTO[] = Object.values(AttributeTypeDTO)
      .filter(t => typeof t !== "number")
      .map(t => t as unknown as AttributeTypeDTO);
  protected newAllowedValue: WritableSignal<string> = signal("");

  // Init
  constructor() {
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
      isArray: [null],
      isMandatory: [null],
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
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched));
  }

  protected errors(field: string, label: string): string[] {
    return this.formService.extractErrors(field, label, this.form);
  }

  protected isNew = computed(() => !this.attribute()?.id);

  protected deleteAllowedValue(allowedValue: string): void {
    let index = this.attribute()?.allowedValues?.indexOf(allowedValue);
    if (index === undefined || index === -1) {
      return;
    }
    this.attribute()?.allowedValues?.splice(index, 1);
  }

  // protected createAllowedValue(): void {
  //   this.newAllowedValue.set("");
  // }

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
}
