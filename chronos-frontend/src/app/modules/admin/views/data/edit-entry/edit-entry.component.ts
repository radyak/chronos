import { Component, computed, effect, inject, signal, Signal, WritableSignal } from '@angular/core';
import { AdminConfirmService } from '../../../services/admin-confirm.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FaIconLibrary, FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IconConstants } from 'src/app/common/constants/icon.constants';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminSchemaService } from '../../../services/admin-schema.service';
import { EntryDTO } from 'src/app/common/model/data/entry.dto';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { DynamicInputComponent } from './dynamic-input/dynamic-input.component';
import { EntryAttributeFormService } from './entry-attribute-form.service';
import { AdminDataService } from '../../../services/admin-data.service';
import { firstValueFrom, map } from 'rxjs';
import { TooltipComponent } from 'src/app/common/components/tooltip/tooltip.component';
import { HistoricalDataService } from 'src/app/modules/public/services/historical-data.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { CREATE_ROUTE_KEYWORD } from '../../../admin.routes';
import { ElementAttributePipe } from 'src/app/common/util/element-attribute.pipe';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'chronos-edit-entry',
  imports: [
    FontAwesomeModule,
    ReactiveFormsModule,
    DynamicInputComponent,
    TooltipComponent,
    ElementAttributePipe,
    DatePipe
  ],
  templateUrl: './edit-entry.component.html',
  styleUrl: './edit-entry.component.scss',
})
export class EditEntryComponent {

  // Subject / Model
  protected entry: WritableSignal<EntryDTO> = signal({
    elementId: '',
    labels: [],
    attributes: {}
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
  private historicalDataService = inject(HistoricalDataService);

  // Derived Data Fields
  protected entryId = toSignal(
    this.route.params.pipe(
      map(params => params['id']),
      map(id => id === CREATE_ROUTE_KEYWORD ? null : id)
    ),
    { initialValue: null }
  );
  protected entryResource = this.historicalDataService.entry(this.entryId);
  protected isNew = computed(() => !this.entryResource.hasValue());
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
      return this.entryAttributeFormService.generateFormGroup(type, this.isNew());
    });

    effect(() => {
      const entry = this.entryResource.value();
      if (!!entry) {
        this.entry.set(entry);
      }
    });

    effect(() => {
      const entry = this.entry();
      this.form().patchValue(entry.attributes);
    });
  }

  // Methods
  protected save(returnAfterSave: boolean = false) {
    this.updateType();
    console.log('Saving entry', this.entry());
    firstValueFrom(this.adminDataService.save(this.entry())).then(
      (entry) => {
        this.entry.set(entry);
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
    const entry = this.entry();
    firstValueFrom(this.adminDataService.delete(entry)).then(
      () => {
        this.back();
      }
    );
  }

  protected updateType(): void {
    const attributes = this.form().getRawValue();
    console.log("Extracted form:", attributes)
    const type = this.typeForm?.getRawValue().type;
    if (type) {
      this.entry.update(e => ({ ...e, labels: [this.typeForm?.getRawValue().type] }));
    }
    this.entry.update(e => ({ ...e, attributes: { ...e.attributes, ...attributes } }));
  }

}
