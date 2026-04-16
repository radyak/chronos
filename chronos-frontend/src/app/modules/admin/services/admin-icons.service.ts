import { inject, Injectable } from '@angular/core';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { fas } from '@fortawesome/free-solid-svg-icons';

@Injectable({
  providedIn: 'root',
})
export class AdminIconsService {

  // Injected dependencies
  faLib = inject(FaIconLibrary);

  /** 
   * Icon names
   * Can be enumerated in html:
   * @example:
   * ```
   * @for(icon of iconNames; track icon) {
   *   <fa-icon [icon]="icon"></fa-icon>
   * }
   * ```
   */
  public readonly iconNames: string[];
  
  constructor() {
    this.faLib.addIconPacks(fas);
    this.iconNames = Object.keys((this.faLib as any).definitions.fas);
  }
}
