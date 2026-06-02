import { Injectable } from '@angular/core';
import { faCircleQuestion, faPen, faPlus, faSave, faTrash, faTriangleExclamation, faXmark } from '@fortawesome/free-solid-svg-icons';

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
  public static readonly ICON_QUESTION = faCircleQuestion;
  public static readonly ICON_WARNING = faTriangleExclamation;
  
}
