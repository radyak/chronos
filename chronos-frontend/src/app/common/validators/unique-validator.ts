import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { Signal } from '@angular/core';

/**
 * Validates that the control value does NOT already exist in the provided signal array.
 *
 * @param existingValuesSignal Signal containing the list of already used strings.
 * @param options Optional normalization settings.
 */
export function uniqueValidator(
  existingValuesSignal: Signal<string[] | undefined>,
  options?: {
    ignoreCase?: boolean;
    trim?: boolean;
  }
): ValidatorFn {
  const { ignoreCase = true, trim = true } = options ?? {};

  const normalize = (value: string): string => {
    let result = value ?? '';

    if (trim) {
      result = result.trim();
    }

    if (ignoreCase) {
      result = result.toLowerCase();
    }

    return result;
  };

  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) {
      return null; // let required validator handle empty values
    }

    const normalizedValue = normalize(String(value));
    const existingValues = existingValuesSignal() ?? [];

    const alreadyExists = existingValues.some(
      item => normalize(item) === normalizedValue
    );

    return alreadyExists
      ? {
          notUnique: {
            value: control.value,
            message: 'This value already exists',
          },
        }
      : null;
  };
}
