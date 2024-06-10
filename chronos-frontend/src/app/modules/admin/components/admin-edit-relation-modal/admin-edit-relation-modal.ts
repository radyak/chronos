import {Component, inject, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Relation} from "../../../../model/relation.model";
import {Entry} from "../../../../model/entry.model";
import {RelationService} from "../../../../service/relation-service";


@Component({
  selector: 'chronos-admin-edit-relation-modal',
  templateUrl: './admin-edit-relation-modal.html',
  styleUrls: ['./admin-edit-relation-modal.scss']
})
export class AdminEditRelationModal {

  activeModal = inject(NgbActiveModal);
  relationService = inject(RelationService)
  editedRelation!: Relation;

  @Input()
  entry!: Entry;

  @Input()
  set relation(relation: Relation) {
    this.editedRelation = {
      ...relation
    };
  }

  get isInverse(): boolean {
    return this.editedRelation.toId === this.entry.id;
  }

  reverse(): void {
    const tempId = this.editedRelation.fromId;
    const temp = this.editedRelation.from;

    this.editedRelation.fromId = this.editedRelation.toId;
    this.editedRelation.from = this.editedRelation.to;

    this.editedRelation.toId = tempId;
    this.editedRelation.to = temp;
  }

}

