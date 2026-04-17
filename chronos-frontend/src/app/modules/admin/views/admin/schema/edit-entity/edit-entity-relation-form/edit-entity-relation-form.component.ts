import { Component, inject, Input, OnInit, WritableSignal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { AttributeAO } from 'src/app/common/model/domain/schema/admin/attribute.ao';
import { RelationAO } from 'src/app/common/model/domain/schema/admin/relation.ao';
import { FormService } from 'src/app/common/util/form.service';
import { IconsService } from 'src/app/modules/admin/services/icons.service';

@Component({
  selector: 'chronos-edit-entity-relation-form',
  imports: [
    FontAwesomeModule,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './edit-entity-relation-form.component.html',
  styleUrl: './edit-entity-relation-form.component.scss',
})
export class EditEntityRelationFormComponent  implements OnInit {
	protected offcanvas = inject(NgbActiveOffcanvas);
  private fb = inject(FormBuilder);
  private formService = inject(FormService);

  // Inputs
  @Input() relation!: RelationAO;

  // Icons
  protected saveIcon = IconsService.ICON_SAVE;
  protected cancelIcon = IconsService.ICON_CANCEL;
  protected editIcon = IconsService.ICON_EDIT;
  protected deleteIcon = IconsService.ICON_DELETE;

  // Form & controls
  protected submitted = false;
  protected form: FormGroup = this.fb.group(
    {
      key: [null,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64),
          // uniqueValidator(this.takenEntityNames)
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

  // Init
  ngOnInit() {
    this.form.patchValue(this.relation); // partial safe update
  }

  // Methods
  protected isInvalid(field: string): boolean {
    const ctrl = this.form?.get(field);
    return !!(ctrl?.invalid && (this.submitted || ctrl?.touched));
  }

  protected errors(field: string, label: string): string[] {
    return this.formService.extractErrors(field, label, this.form);
  }

  protected confirm() {
    this.submitted = true;
    const relation = this.form?.getRawValue();
    this.offcanvas.close(relation);
  }

  protected cancel() {
    this.offcanvas.dismiss();
  }

}
