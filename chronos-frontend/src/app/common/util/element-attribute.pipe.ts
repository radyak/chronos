import { Pipe, PipeTransform } from '@angular/core';
import { DataElementDTO } from '../model/data/data-element.dto';

@Pipe({
  name: 'attribute',
})
export class ElementAttributePipe implements PipeTransform {

  transform(value: DataElementDTO, ...args: unknown[]): unknown {
    return value.properties[args[0] as string];
  }

}
