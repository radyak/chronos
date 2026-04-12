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
  
  // icon names
  // Enumerate in html with:
  // @for(icon of iconNames; track icon) {
  //   <fa-icon [icon]="icon"></fa-icon>
  // }
  public readonly iconNames: string[] = [];
  
  constructor() {
    this.faLib.addIconPacks(fas);

    // library.add(fas);
    this.iconNames = Object.keys((this.faLib as any).definitions.fas);
  }
}
