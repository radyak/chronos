import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faClose, faSearch} from "@fortawesome/free-solid-svg-icons";
import { Entity } from 'src/app/model/domain/entity.model';

interface TableEntityRepresentation extends Entity {
  _original: Entity
}

export interface EntitiesTableAction {
  fn: (e: Entity) => void,
  icon: IconDefinition,
  color?: 'warn' | 'success' | 'danger'
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
    DatePipe,
    NgForOf,
    NgClass,
    NgIf
  ]
})
export class EntitiesTableComponent {

  searchIcon = faSearch;
  clearIcon = faClose;

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
    this.viewEntities = (entities || []).map(entity => {
      return {
        ...entity,
        _original: entity
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
    return (this.viewEntities || []).filter(entity =>
      this.tagQuery ? entity.key?.match(this.tagQuery) : true
    );
  }

  isEntitySelected(entity: Entity): boolean {
    return this.selectedEntities.has(entity);
  }

  toggleEntitySelection(entity: Entity): void {
    if (this.isEntitySelected(entity)) {
      this.selectedEntities.delete(entity);
    } else {
      this.selectedEntities.add(entity);
    }
    this.onSelect.emit(Array.from(this.selectedEntities));
  }

  allEntitiesSelected(): boolean {
    return !(this.entities || []).find(entity => !this.selectedEntities.has(entity));
  }

  toggleAllEntitiesSelection(): void {
    if (!this.allEntitiesSelected()) {
      this.entities?.forEach(entity => this.selectedEntities.add(entity));
    } else {
      this.entities?.forEach(entity => this.selectedEntities.delete(entity));
    }
    this.onSelect.emit(Array.from(this.selectedEntities));
  }

}
