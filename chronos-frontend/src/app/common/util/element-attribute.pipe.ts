import { Pipe, PipeTransform } from '@angular/core';
import { EntryDTO } from '../model/data/data-element.dto';

@Pipe({
  name: 'attribute',
})
export class ElementAttributePipe implements PipeTransform {

  transform(value: EntryDTO, ...args: unknown[]): unknown {
    return value.properties[args[0] as string];
  }

}
