import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { EntityDTO } from 'src/app/common/model/domain/schema/entity.dto';
import { CREATE_ROUTE_KEYWORD } from '../../../admin.routes';
import { SchemaService } from '../../../services/schema.service';
import { IconsService } from '../../../services/icons.service';
import { firstValueFrom } from 'rxjs';
import { fas } from '@fortawesome/free-solid-svg-icons';

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

  // Injected dependencies
  protected schemaService = inject(SchemaService);
  protected router = inject(Router);
  protected route = inject(ActivatedRoute);
  protected faLib = inject(FaIconLibrary);

  protected reloadTrigger = signal(0);
  protected schema = this.schemaService.schemaResource(this.reloadTrigger);

  protected newIcon = IconsService.ICON_ADD;
  protected editIcon = IconsService.ICON_EDIT;
  protected deleteIcon = IconsService.ICON_DELETE;

  constructor() {
      this.faLib.addIconPacks(fas);
  }

  protected newEntity(): void {
    this.router.navigate([CREATE_ROUTE_KEYWORD], { relativeTo: this.route });
  }

  protected editEntity(entity: EntityDTO): void {
    this.router.navigate([entity.key], { relativeTo: this.route });
  }

  protected deleteEntity(entity: EntityDTO): void {
    firstValueFrom(this.schemaService.deleteEntity(entity)).then(
      () => {
        this.reloadTrigger.update(v => v + 1);
      }
    );
  }

}
