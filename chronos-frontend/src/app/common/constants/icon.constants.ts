import { Injectable } from '@angular/core';
import { faCircleNotch, faCircleQuestion, faMagnifyingGlass, faPen, faPlus, faSave, faSpinner, faTrash, faTriangleExclamation, faXmark } from '@fortawesome/free-solid-svg-icons';

@Injectable({
  providedIn: 'root',
})
export class IconConstants {

  // public readonly fields
  // icons
  public static readonly ICON_ADD = faPlus;
  public static readonly ICON_SAVE = faSave;
  public static readonly ICON_CANCEL = faXmark;
  public static readonly ICON_EDIT = faPen;
  public static readonly ICON_DELETE = faTrash;
  public static readonly ICON_HELP = faCircleQuestion;
  public static readonly ICON_WARNING = faTriangleExclamation;
  public static readonly ICON_SEARCH = faMagnifyingGlass;
  public static readonly ICON_WAITING = faCircleNotch;
}
