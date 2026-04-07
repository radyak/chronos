import { Component, effect, inject, model, ModelSignal } from '@angular/core';
import { FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';

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
  private fb = inject(NonNullableFormBuilder);
  private activeModal = inject(NgbActiveModal);

  // Input
  protected attribute: ModelSignal<AttributeAO | undefined> = model();

  // Form & controls
  protected form!: FormGroup;

  // Init
  constructor() {
    this.form = this.fb.group({
      key: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64)
        ],
      ],
      explanation: ['', 
        [
          Validators.minLength(3),
          Validators.maxLength(255)
        ]
      ],
      examples: ['', 
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
