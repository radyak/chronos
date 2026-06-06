import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TypeAO } from 'src/app/common/model/schema/admin/type.ao';

@Injectable({
  providedIn: 'root'
})
export class EntryAttributeFormService {

  // Injected Dependencies
  private fb: FormBuilder = inject(FormBuilder);

  // Methods
  public generateFormGroup(type: TypeAO | null, isNewEntry: boolean): FormGroup {
    const group: { [key: string]: any } = {};

    const attributes = [
      ...(type?.defaultAttributes ?? []),
      ...(type?.attributes ?? []),
    ]

    attributes.forEach(attr => {
      const validators = [];
      if (attr.isMandatory) {
        validators.push(Validators.required);
      }
      if (attr.isUnique) {
        // TODO: add unique validation with backend
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
      group[attr.key!] = [{value: null, disabled: !isNewEntry && !attr.isChangeable}, validators];
    });

    return this.fb.group(group);
  };
}
