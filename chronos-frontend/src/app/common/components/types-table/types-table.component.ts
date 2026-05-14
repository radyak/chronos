import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import { NgClass } from "@angular/common";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";
import {faClose, faEllipsis, faSearch} from "@fortawesome/free-solid-svg-icons";
import { Type } from 'src/app/common/model/domain/type.model';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

interface TableTypeRepresentation extends Type {
  _original: Type
}

export interface TypesTableAction {
  fn: (e: Type) => void,
  icon: IconDefinition,
  color?: 'warn' | 'success' | 'danger',
  text: string
}

export interface TypesTableSearch {
  key?: string,
  from?: string,
  to?: string
}

@Component({
    selector: 'chronos-types-table',
    templateUrl: './types-table.component.html',
    styleUrls: ['./types-table.component.scss'],
    imports: [
      FormsModule,
      FontAwesomeModule,
      NgClass,
      NgbDropdownModule
    ]
})
export class TypesTableComponent {

  searchIcon = faSearch;
  clearIcon = faClose;
  ellipsisIcon = faEllipsis;

  @Output()
  typeClick: EventEmitter<Type> = new EventEmitter<Type>();

  @Input()
  search: TypesTableSearch = {};

  @Input()
  searchable = true;

  @Output()
  submit: EventEmitter<void> = new EventEmitter<void>();

  @Input()
  actions: Array<TypesTableAction> = [];

  @Input()
  set types(types: Array<Type> | null) {
    this.viewTypes = (types || []).map(type => {
      return {
        ...type,
        _original: type
      }
    })
  }

  get types() {
    return this.viewTypes.map(ve => ve._original);
  }

  protected viewTypes: Array<TableTypeRepresentation> = [];

  @Input()
  public selectable = false;

  protected selectedTypes: Set<Type> = new Set<Type>();

  @Output()
  onSelect: EventEmitter<Array<Type>> = new EventEmitter<Array<Type>>();

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

  filteredTypes(): Array<TableTypeRepresentation> {
    return (this.viewTypes || []).filter(type =>
      this.tagQuery ? type.key?.match(this.tagQuery) : true
    );
  }

  isTypeSelected(type: Type): boolean {
    return this.selectedTypes.has(type);
  }

  toggleTypeSelection(type: Type): void {
    if (this.isTypeSelected(type)) {
      this.selectedTypes.delete(type);
    } else {
      this.selectedTypes.add(type);
    }
    this.onSelect.emit(Array.from(this.selectedTypes));
  }

  allTypesSelected(): boolean {
    return !(this.types || []).find(type => !this.selectedTypes.has(type));
  }

  toggleAllTypesSelection(): void {
    if (!this.allTypesSelected()) {
      this.types?.forEach(type => this.selectedTypes.add(type));
    } else {
      this.types?.forEach(type => this.selectedTypes.delete(type));
    }
    this.onSelect.emit(Array.from(this.selectedTypes));
  }

}
