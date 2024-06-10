import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {filter, map, Observable, of} from "rxjs";
import {AdminRelationTypesService} from "../../services/admin-relation-types.service";
import {RelationType} from "../../../../model/relation-type.model";

@Component({
  selector: 'chronos-admin-relation-type-select-input',
  templateUrl: './admin-relation-type-select-input.component.html',
  styleUrls: ['./admin-relation-type-select-input.component.scss']
})
export class AdminRelationTypeSelectInputComponent implements OnInit {

  @Input()
  relationType?: RelationType;

  @Input()
  showInverted?: boolean = false;

  @Output()
  relationTypeChange: EventEmitter<RelationType> = new EventEmitter<RelationType>();

  relationTypes$: Observable<Array<RelationType>> = of();

  relationTypeFilter: string = '';

  constructor(private adminRelationTypesService: AdminRelationTypesService) {

  }

  ngOnInit() {
    this.relationTypes$ = this.adminRelationTypesService.allRelationTypes();
  }

  search(): void {
  }

  select(relationType: RelationType): void {
    this.relationType = relationType;
    this.relationTypeChange.emit(relationType);
  }

  filteredRelationTypes(types: Array<RelationType> | null): Array<RelationType> {
    return (types || []).filter(type =>
        type.label.toLowerCase().includes(this.relationTypeFilter.toLowerCase())
        || type.inverseRelationLabel?.toLowerCase().includes(this.relationTypeFilter.toLowerCase())
    );
  }

}
