import { Component, effect, inject, model, ModelSignal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';
import { AttributeTypeDTO } from 'src/app/common/model/domain/schema/attribute-type.dto';

@Component({
  selector: 'chronos-edit-entity-attribute-dialog',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './edit-entity-attribute-dialog.component.html',
  styleUrl: './edit-entity-attribute-dialog.component.scss',
})
export class EditEntityAttributeDialogComponent {

  // Dependencies
  private fb = inject(FormBuilder);
  private activeModal = inject(NgbActiveModal);

  // Input
  protected attribute: ModelSignal<AttributeAO | undefined> = model();

  // Form & controls
  protected form!: FormGroup;
  // type AttributeTypeKey = keyof typeof AttributeTypeDTO
  protected types: AttributeTypeDTO[] = Object.values(AttributeTypeDTO)
      .filter(t => typeof t !== "number")
      .map(t => t as unknown as AttributeTypeDTO);

  // Init
  constructor() {
    this.form = this.fb.group({
      key: [
        null,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64)
        ],
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
      type: [
        null, 
        [
          Validators.required
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

  confirm(): void {
    if (this.form.invalid) {
      return;
    }
    const attributeResult = this.form.getRawValue();
    this.activeModal.close(attributeResult);
  }

  cancel(): void {
    this.activeModal.dismiss()
  }
}
