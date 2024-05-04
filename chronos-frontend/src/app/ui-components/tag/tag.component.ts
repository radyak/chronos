import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Tag} from "../../model/tag.model";
import {CommonModule} from "@angular/common";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faPlus, faXmark} from "@fortawesome/free-solid-svg-icons";
import {isBright} from "../../util/color-brightness.function";

@Component({
  standalone: true,
  selector: 'chronos-tag',
  templateUrl: './tag.component.html',
  styleUrls: ['./tag.component.scss'],
  imports: [
    CommonModule,
    FontAwesomeModule
  ]
})
export class TagComponent {

  closeIcon = faXmark;
  addIcon = faPlus;

  @Input()
  tag?: Tag;

  @Input()
  action?: 'remove' | 'add';

  @Input()
  size: 'small' | 'normal' = 'normal';

  @Input()
  hideCategory = false;

  @Output()
  actionTriggered: EventEmitter<void> = new EventEmitter<void>();

  isBright(color?: string) {
    return color && isBright(color);
  }
}
