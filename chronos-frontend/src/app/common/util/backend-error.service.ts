import { Injectable } from '@angular/core';
import { ApiErrorDTO } from '../model/error-response.dto';

@Injectable({
  providedIn: 'root',
})
export class BackendErrorService {
  
  private readonly ERROR_MAP: Record<string, (key: string, params?: any) => string> = {
    'org.chronos.schema.error.invalid-length': (key: string, params: any) => `"${key}" may have between ${params.min} and ${params.max} characters`,
    'org.chronos.schema.error.not-specified': (key: string, params: any) => `"${key}" is not specified`,
    'org.chronos.schema.error.duplicate-key': (key: string, params: any) => `"${key}" already exists on another element`,
    'org.chronos.data.error.unique': (key: string, params: any) => `The ${key} "${params.value}" already exists on another element`,
    // To be continued ...
  }

  public extractErrors(field: string, label: string, apiErrors: ApiErrorDTO[]): string[] {
    if (!apiErrors) {
      return [];
    }
    const errors = apiErrors.filter(e => e.field === field);

    if (!errors || errors.length === 0) {
      return [];
    }
    return errors.map(e => {
      const templateFn = this.ERROR_MAP[e.message] || this.ERROR_MAP[e.constraint];
      if (!!templateFn) {
        return templateFn(label, e.arguments);
      }
      return e.message;
    });
  }

}
