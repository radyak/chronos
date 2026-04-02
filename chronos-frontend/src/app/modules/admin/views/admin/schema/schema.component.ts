import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { SchemaService } from '../../../services/schema.service';
import { EntityDTO } from 'src/app/common/model/domain/schema/entity.dto';
import { CREATE_ROUTE_KEYWORD } from '../../../admin.routes';

@Component({
  selector: 'chronos-schema',
  imports: [
    FontAwesomeModule,
    RouterModule
  ],
  templateUrl: './schema.component.html',
  styleUrl: './schema.component.scss',
})
export class SchemaComponent{
  protected schema = inject(SchemaService).schemaResource();
  protected router = inject(Router);
  protected route = inject(ActivatedRoute);

  protected newIcon = faPlus;

  protected newEntity(): void {
    this.router.navigate([CREATE_ROUTE_KEYWORD], { relativeTo: this.route });
  }

  protected editEntity(entity: EntityDTO): void {
    this.router.navigate([entity.key], { relativeTo: this.route });
  }

}
