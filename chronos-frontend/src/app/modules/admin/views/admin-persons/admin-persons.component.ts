import {Component} from '@angular/core';
import {Observable, of} from "rxjs";
import {faCopy, faListCheck, faPenToSquare, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Params, Router, RouterModule} from "@angular/router";
import {AdminConfirmService} from "../../services/admin-confirm.service";
import {AbstractQueryDrivenComponent} from "../../../../common/components/abstract-query-driven-component.directive";
import {EntitiesTableAction, EntitiesTableComponent, EntitiesTableSearch} from "../../../../common/components/entities-table/entities-table.component";
import { Entity } from 'src/app/common/model/domain/entityPO.model';
import { AdminPersonService } from '../../person/admin-person.service';
import { PersonService } from '../../person/entries.service';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'chronos-admin-persons',
  templateUrl: './admin-persons.component.html',
  styleUrls: ['./admin-persons.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FontAwesomeModule,
    EntitiesTableComponent,
    RouterModule
  ]
})
export class AdminPersonsComponent extends AbstractQueryDrivenComponent {

  newIcon = faPlus;
  batchActionIcon = faListCheck;

  entities$: Observable<Array<Entity>> = of([]);
  selectedEntities: Array<Entity> = [];

  entitySearch: EntitiesTableSearch = {};
  tableActions: Array<EntitiesTableAction> = [
    {
      fn: (entityPO: Entity) => this.editEntity(entityPO),
      icon: faPenToSquare,
      text: 'Edit'
    },
    {
      fn: (entityPO: Entity) => this.copyEntity(entityPO),
      icon: faCopy,
      text: 'Copy'
    },
    {
      fn: (entityPO: Entity) => this.deleteEntity(entityPO),
      icon: faTrash,
      color: 'danger',
      text: 'Delete'
    }
  ]

  constructor(private adminEntitiesService: AdminPersonService,
              private entityService: PersonService,
              protected override router: Router,
              protected override route: ActivatedRoute,
              private confirmService: AdminConfirmService) {
    super(router, route);
  }

  override search(): void {
    this.entities$ = this.entityService.find(this.toParams())
  }

  override toClassFields(params: Params) {
    this.entitySearch.from = params['from'];
    this.entitySearch.from = params['to'];
  }

  override toParams(): Params {
    return {
      from: this.entitySearch.from,
      to: this.entitySearch.to
    };
  }

  editEntity(entityPO: Entity): void {
    this.router.navigate([entityPO.id], {relativeTo: this.route});
  }

  newEntity(): void {
    this.router.navigate(['new'], {relativeTo: this.route});
  }

  deleteEntity(entityPO: Entity): void {
    this.confirmService.confirm(
      "Confirm deleting entityPO",
      `Do you want to delete entityPO ${entityPO.key}?`
    )
    .then(() => {
      this.adminEntitiesService.delete(entityPO).subscribe(() => this.search())
    });
  }

  copyEntity(entityPO: Entity): void {
    this.router.navigate([entityPO.id, "copy"], {relativeTo: this.route});
  }

}

