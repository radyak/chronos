import { inject, Injectable } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { SchemaTypeAO } from 'src/app/common/model/schema/admin/type.ao';
import { UniquenessValidatorService } from 'src/app/common/validators/api-unique-validator';

@Injectable({
  providedIn: 'root'
})
export class EntryAttributeFormService {

  // Injected Dependencies
  private fb: FormBuilder = inject(FormBuilder);
  private uniquenessValidatorService: UniquenessValidatorService = inject(UniquenessValidatorService);

  // Methods
  public generateFormGroup(type: SchemaTypeAO | null, isNewEntry: boolean, elementId?: string): FormGroup {
    const group: { [key: string]: any } = {};

    const attributes = [
      ...(type?.defaultAttributes ?? []),
      ...(type?.attributes ?? []),
    ]

    attributes.forEach(attr => {
      const validators: ValidatorFn[] = [];
      const asyncValidators: AsyncValidatorFn[] = [];
      if (attr.isMandatory) {
        validators.push(Validators.required);
      }
      if (attr.isUnique) {
        asyncValidators.push(this.uniquenessValidatorService.apiUniqueValidator(attr.key!, elementId))
      }
      if (attr.valuePattern) {
        validators.push(Validators.pattern(attr.valuePattern));
      }
      if (attr.valueRange) {
        const [min, max] = attr.valueRange.split('-');
        if (min) {
          validators.push(Validators.min(Number(min)));
        }
        if (max) {
          validators.push(Validators.max(Number(max)));
        }
      }
      group[attr.key!] = [{value: null, disabled: !isNewEntry && !attr.isChangeable}, validators, asyncValidators];
    });

    return this.fb.group(group);
  };
}
