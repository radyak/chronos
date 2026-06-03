import { Component, computed, effect, inject, signal, Signal, WritableSignal } from '@angular/core';
import { AdminConfirmService } from '../../../services/admin-confirm.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IconConstants } from 'src/app/common/constants/icon.constants';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminSchemaService } from '../../../services/admin-schema.service';
import { EntryDTO } from 'src/app/common/model/data/data-element.dto';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { DynamicInputComponent } from './dynamic-input/dynamic-input.component';
import { EntryAttributeFormService } from './entry-attribute-form.service';
import { AdminDataService } from '../../../services/admin-data.service';
import { firstValueFrom } from 'rxjs';
import { TooltipComponent } from 'src/app/common/components/tooltip/tooltip.component';

@Component({
  selector: 'chronos-edit-entry',
  imports: [
    FontAwesomeModule,
    ReactiveFormsModule,
    DynamicInputComponent,
    TooltipComponent
  ],
  templateUrl: './edit-entry.component.html',
  styleUrl: './edit-entry.component.scss',
})
export class EditEntryComponent {

  // Subject / Model
  protected entry: WritableSignal<EntryDTO> = signal({
    elementId: '',
    labels: [],
    properties: {}
  });

  // Icons
  protected cancelIcon = IconConstants.ICON_CANCEL;
  protected saveIcon = IconConstants.ICON_SAVE;
  protected deleteIcon = IconConstants.ICON_DELETE;
  protected questionIcon = IconConstants.ICON_HELP;

  // Dependencies
  private route: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router);
  private adminConfirmService: AdminConfirmService = inject(AdminConfirmService);
  private fb: FormBuilder = inject(FormBuilder);
  private faLib = inject(FaIconLibrary);
  private entryAttributeFormService = inject(EntryAttributeFormService);
  private adminDataService = inject(AdminDataService);

  // Derived Data Fields
  protected isNew = computed(() => true);
  protected schema = inject(AdminSchemaService).allTypes();

  // Derived Signals
  protected currentEntryTypeKey = computed(() => {
    const labels = this.entry().labels;
    return labels && labels.length > 0 ? labels[0] : null;
  });
  protected selectedType = computed(() => {
    const selectedTypeKey = this.currentEntryTypeKey();
    return this.schema.value()?.find(type => type.key === selectedTypeKey) ?? null;
  });

  // Form & controls
  protected typeForm?: FormGroup;
  protected form: Signal<FormGroup>;

  // Init
  constructor() {
    this.faLib.addIconPacks(fas);

    this.typeForm = this.fb.group(
      {
        type: [null,
          [
            Validators.required,
          ],
        ],
      }
    );
    this.form = computed(() => {
      const type = this.selectedType();
      return this.entryAttributeFormService.generateFormGroup(type);
    });

    effect(() => {
      const entry = this.entry();
      this.form().patchValue(entry.properties);
    });
  }

  // Methods
  protected save(returnAfterSave: boolean = false) {
    this.updateType();
    console.log('Saving entry', this.entry());
    firstValueFrom(this.adminDataService.save(this.entry())).then(
      () => {
        if (returnAfterSave) {
          this.back();
        }
      },
      (err) => {
        // Nothing todo
      }
    );
  }

  protected cancel() {
    this.adminConfirmService.confirm(
      'Confirm Cancel Edit',
      'Do you want to cancel editing this entry and leave without saving?'
    ).then(
      () => {
        this.back()
      },
      () => {
        // Nothing todo
      }
    )
  }

  protected back(): void {
    this.router.navigate(['..'], { relativeTo: this.route })
  }

  protected delete() {
    // TODO
  }

  protected updateType(): void {
    const properties = this.form().getRawValue();
    this.entry.update(e => ({ ...e, labels: [this.typeForm?.getRawValue().type] }));
    this.entry.update(e => ({ ...e, properties: { ...e.properties, ...properties } }));
  }

}
