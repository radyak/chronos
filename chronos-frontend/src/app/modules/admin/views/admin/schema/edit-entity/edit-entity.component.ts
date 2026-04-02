import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { faCross, faSave, faX, faXmark } from '@fortawesome/free-solid-svg-icons';
import { EntityDTO } from 'src/app/common/model/domain/schema/entity.dto';
import { SchemaService } from 'src/app/modules/admin/services/schema.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { CREATE_ROUTE_KEYWORD } from 'src/app/modules/admin/admin.routes';

@Component({
  selector: 'chronos-edit-entity',
  imports: [
    RouterModule,
    FontAwesomeModule
  ],
  templateUrl: './edit-entity.component.html',
  styleUrl: './edit-entity.component.scss',
})
export class EditEntityComponent {
  protected route = inject(ActivatedRoute);
  protected entityId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected entity = inject(SchemaService).schemaEntityResource(this.entityId);
  protected isNew = computed(() => !this.entity.value()?.id)
  protected router = inject(Router);
  protected adminConfirmService = inject(AdminConfirmService);

  protected saveIcon = faSave;
  protected cancelIcon = faXmark;

  protected save(): void {
    
  }

  protected cancel(): void {
    this.adminConfirmService.confirm(
      'Confirm Cancel Edit',
      'Do you want to cancel editing this schema entity and leave without saving?'
    ).then(
      () => {
        this.router.navigate(['..'], { relativeTo: this.route })
      },
      () => {
        console.debug('Editing cancelled')
      }
    )
  }

}
