import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class FormService {
  
  private readonly ERROR_MAP: Record<string, (key: string, params?: any) => string> = {
    required: (key: string) => `"${key}" is required`,
    minlength: (key: string, params: any) => `"${key}" must have at least ${params.requiredLength} characters`,
    maxlength: (key: string, params: any) => `"${key}" may have at most ${params.requiredLength} characters`,
    notUnique: (key: string) => `"${key}" must be unique`,
  }

  public extractErrors(field: string, label: string, form: FormGroup): string[] {
    const errors = form.get(field)?.errors;
    if (!errors) {
      return [];
    }
    return Object.keys(errors).map(errorKey => {
      const errorGenerator = this.ERROR_MAP[errorKey];
      const errorParams = errors[errorKey];
      return errorGenerator?.(label, errorParams);
    }).filter(error => !!error);
  }

}
