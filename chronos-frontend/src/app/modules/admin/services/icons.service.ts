import { inject, Injectable } from '@angular/core';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faPen, faPlus, fas, faSave, faTrash, faXmark } from '@fortawesome/free-solid-svg-icons';

@Injectable({
  providedIn: 'root',
})
export class IconsService {

  // Injected dependencies
  faLib = inject(FaIconLibrary);
  
  // public readonly fields
  // icons
  public static readonly ICON_ADD = faPlus;
  public static readonly ICON_SAVE = faSave;
  public static readonly ICON_CANCEL = faXmark;
  public static readonly ICON_EDIT = faPen;
  public static readonly ICON_DELETE = faTrash;
  
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
  public get iconNames(): string[] {
    if (!this._iconNames) {
      this.faLib.addIconPacks(fas);
      this._iconNames = Object.keys((this.faLib as any).definitions.fas);
    }
    return this._iconNames ?? [];
  };
  private _iconNames?: string[];
  
  constructor() {
  }
}
