import {Component, inject, Input} from '@angular/core';
import {
  NgbActiveModal,
  NgbDropdown,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle
} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {TagSelectionComponent} from "../../../../ui-components/tag-selection/tag-selection.component";
import {Relation} from "../../../../model/relation.model";
import {DateInputComponent} from "../../../../ui-components/date-input/date-input.component";
import {Entry} from "../../../../model/entry.model";


@Component({
  standalone: true,
  selector: 'chronos-admin-edit-relation-modal',
  templateUrl: './admin-edit-relation-modal.html',
  imports: [
    NgIf,
    TagSelectionComponent,
    AsyncPipe,
    DateInputComponent,
    NgForOf,
    NgbDropdown,
    NgbDropdownItem,
    NgbDropdownMenu,
    NgbDropdownToggle
  ],
  styleUrls: ['./admin-edit-relation-modal.scss']
})
export class AdminEditRelationModal {

  activeModal = inject(NgbActiveModal);
  editedRelation!: Relation;

  @Input()
  entry!: Entry;

  @Input()
  set relation(relation: Relation) {
    this.editedRelation = {
      ...relation
    };
  }

}

