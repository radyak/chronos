import { Pipe, PipeTransform } from '@angular/core';
import { EntryDTO } from '../model/data/entry.dto';

@Pipe({
  name: 'attribute',
})
export class ElementAttributePipe implements PipeTransform {

  transform(value: EntryDTO, ...args: unknown[]): unknown {
    return value.attributes[args[0] as string];
  }

}
