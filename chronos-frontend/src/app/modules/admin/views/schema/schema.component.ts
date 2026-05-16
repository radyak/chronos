import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CREATE_ROUTE_KEYWORD } from '../../admin.routes';
import { AdminSchemaService } from '../../services/admin-schema.service';
import { IconConstants } from '../../../../common/constants/icon.constants';
import { firstValueFrom } from 'rxjs';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { TypeAO } from 'src/app/common/model/schema/admin/type.ao';

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
  protected schemaService = inject(AdminSchemaService);
  protected router = inject(Router);
  protected route = inject(ActivatedRoute);
  protected faLib = inject(FaIconLibrary);

  protected reloadTrigger = signal(0);
  protected schema = this.schemaService.allTypes(this.reloadTrigger);

  protected newIcon = IconConstants.ICON_ADD;
  protected editIcon = IconConstants.ICON_EDIT;
  protected deleteIcon = IconConstants.ICON_DELETE;

  constructor() {
      this.faLib.addIconPacks(fas);
  }

  protected newType(): void {
    this.router.navigate([CREATE_ROUTE_KEYWORD], { relativeTo: this.route });
  }

  protected editType(type: TypeAO): void {
    this.router.navigate([type.key], { relativeTo: this.route });
  }

  protected deleteType(type: TypeAO): void {
    firstValueFrom(this.schemaService.deleteType(type)).then(
      () => {
        this.reloadTrigger.update(v => v + 1);
      }
    );
  }

}
