import { Component, computed, effect, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { faSave, faXmark } from '@fortawesome/free-solid-svg-icons';
import { SchemaService } from 'src/app/modules/admin/services/schema.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { firstValueFrom, map } from 'rxjs';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminConfirmService } from 'src/app/modules/admin/services/admin-confirm.service';
import { CREATE_ROUTE_KEYWORD } from 'src/app/modules/admin/admin.routes';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { EditEntityFormMapper } from './edit-entity-form.mapper';

@Component({
  selector: 'chronos-edit-entity',
  imports: [
    RouterModule,
    FontAwesomeModule,
    ReactiveFormsModule
  ],
  templateUrl: './edit-entity.component.html',
  styleUrl: './edit-entity.component.scss',
})
export class EditEntityComponent {

  // Dependencies
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private adminConfirmService = inject(AdminConfirmService);
  private schemaService = inject(SchemaService);
  private fb = inject(NonNullableFormBuilder);
  private notificationService = inject(NotificationService);

  // Icons
  protected saveIcon = faSave;
  protected cancelIcon = faXmark;

  // Derived Data Fields
  protected entityId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected entityResource = this.schemaService.schemaEntityResource(this.entityId);
  protected isNew = computed(() => !this.entityResource.value()?.id);
  protected form = this.fb.group(
    {
      key: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(64)
        ],
        // [this.usernameTakenValidator()]
      ],
      explanation: ['', 
        [
          Validators.minLength(3),
          Validators.maxLength(255)
        ]
      ],
      examples: ['', 
        [
          Validators.minLength(3),
          Validators.maxLength(255)
        ]
      ]
    },
    // { validators: this.passwordMatchValidator }
  );


  // Init
  constructor() {
    effect(() => {
      const entity = this.entityResource.value();

      if (entity) {
        this.form.patchValue(entity); // partial safe update
      }
    });
  }


  // Methods
  protected save(): void {
    const entity = EditEntityFormMapper.toAO(this.form.getRawValue(), this.entityResource.value());
    console.log("Saving entity:", entity);
    firstValueFrom(this.schemaService.saveEntity(entity)).then(
      () => {
        this.notificationService.success(`Entity "${entity.key}" saved successfully`);
        this.back();
      },
      (err) => {
        this.notificationService.error(`Error while saving entity "${entity.key}"`);
        console.log("Error saving", err)
      }
    );
  }

  protected cancel(): void {
    this.adminConfirmService.confirm(
      'Confirm Cancel Edit',
      'Do you want to cancel editing this schema entity and leave without saving?'
    ).then(
      () => {
        this.back()
      },
      () => {
        console.debug('Editing cancelled')
      }
    )
  }

  protected back(): void {
    this.router.navigate(['..'], { relativeTo: this.route })
  }

}
