import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faClose, faEllipsis, faSearch} from "@fortawesome/free-solid-svg-icons";
import { Entity } from 'src/app/common/model/domain/entityPO.model';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

interface TableEntityRepresentation extends Entity {
  _original: Entity
}

export interface EntitiesTableAction {
  fn: (e: Entity) => void,
  icon: IconDefinition,
  color?: 'warn' | 'success' | 'danger',
  text: string
}

export interface EntitiesTableSearch {
  key?: string,
  from?: string,
  to?: string
}

@Component({
  standalone: true,
  selector: 'chronos-enitities-table',
  templateUrl: './entities-table.component.html',
  styleUrls: ['./entities-table.component.scss'],
  imports: [
    FormsModule,
    FontAwesomeModule,
    NgForOf,
    NgIf,
    NgClass,
    NgbDropdownModule
  ]
})
export class EntitiesTableComponent {

  searchIcon = faSearch;
  clearIcon = faClose;
  ellipsisIcon = faEllipsis;

  @Output()
  entityClick: EventEmitter<Entity> = new EventEmitter<Entity>();

  @Input()
  search: EntitiesTableSearch = {};

  @Input()
  searchable = true;

  @Output()
  submit: EventEmitter<void> = new EventEmitter<void>();

  @Input()
  actions: Array<EntitiesTableAction> = [];

  @Input()
  set entities(entities: Array<Entity> | null) {
    this.viewEntities = (entities || []).map(entityPO => {
      return {
        ...entityPO,
        _original: entityPO
      }
    })
  }

  get entities() {
    return this.viewEntities.map(ve => ve._original);
  }

  protected viewEntities: Array<TableEntityRepresentation> = [];

  @Input()
  public selectable = false;

  protected selectedEntities: Set<Entity> = new Set<Entity>();

  @Output()
  onSelect: EventEmitter<Array<Entity>> = new EventEmitter<Array<Entity>>();

  protected tagQuery = '';

  clearKey() {
    this.search.key = '';
    this.submit.emit();
  }

  clearDateQuery() {
    this.search.from = '';
    this.search.to = '';
    this.submit.emit();
  }

  filteredEntities(): Array<TableEntityRepresentation> {
    return (this.viewEntities || []).filter(entityPO =>
      this.tagQuery ? entityPO.key?.match(this.tagQuery) : true
    );
  }

  isEntitySelected(entityPO: Entity): boolean {
    return this.selectedEntities.has(entityPO);
  }

  toggleEntitySelection(entityPO: Entity): void {
    if (this.isEntitySelected(entityPO)) {
      this.selectedEntities.delete(entityPO);
    } else {
      this.selectedEntities.add(entityPO);
    }
    this.onSelect.emit(Array.from(this.selectedEntities));
  }

  allEntitiesSelected(): boolean {
    return !(this.entities || []).find(entityPO => !this.selectedEntities.has(entityPO));
  }

  toggleAllEntitiesSelection(): void {
    if (!this.allEntitiesSelected()) {
      this.entities?.forEach(entityPO => this.selectedEntities.add(entityPO));
    } else {
      this.entities?.forEach(entityPO => this.selectedEntities.delete(entityPO));
    }
    this.onSelect.emit(Array.from(this.selectedEntities));
  }

}
