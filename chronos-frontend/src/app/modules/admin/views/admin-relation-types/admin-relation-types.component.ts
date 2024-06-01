import {Component} from '@angular/core';
import {Observable, of} from "rxjs";
import {faPenToSquare, faPlus, faSearch} from "@fortawesome/free-solid-svg-icons";
import {RelationType} from "../../../../model/relation-type.model";
import {AdminRelationTypesService} from "../../services/admin-relation-types.service";

@Component({
  selector: 'chronos-admin-relation-types',
  templateUrl: './admin-relation-types.component.html',
  styleUrls: ['./admin-relation-types.component.scss']
})
export class AdminRelationTypesComponent {

  relationTypes$: Observable<Array<RelationType>> = of([]);
  currentRelationType?: RelationType;
  searchRelationType: string = '';

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;

  constructor(private adminRelationTypesService: AdminRelationTypesService) {
  }

  ngOnInit(): void {
    this.relationTypes$ = this.adminRelationTypesService.allRelationTypes();
  }

  editRelationType(relationType: RelationType): void {
    this.currentRelationType = {...relationType};
  }

  newRelationType(): void {
    this.currentRelationType = {
      name: ""
    };
  }

  saveRelationType(): void {
    if (!this.currentRelationType) {
      return
    }
    this.adminRelationTypesService.saveRelationType(this.currentRelationType).subscribe(() => {
      delete this.currentRelationType;
      this.relationTypes$ = this.adminRelationTypesService.allRelationTypes();
    })
  }

  cancelEditRelationType(): void {
    delete this.currentRelationType;
  }

  deleteRelationType(relationType: RelationType): void {
    this.adminRelationTypesService.deleteRelationType(relationType).subscribe(() => {
      delete this.currentRelationType;
      this.relationTypes$ = this.adminRelationTypesService.allRelationTypes();
    })
  }

  filterRelationType(relationType: RelationType): boolean {
    return relationType.name.toLowerCase().includes(this.searchRelationType.toLowerCase())
  }

}
