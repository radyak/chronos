import { Component, computed, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { EntityAO } from 'src/app/common/model/domain/schema/admin/entity.ao';
import { SchemaService } from 'src/app/modules/admin/services/schema.service';

@Component({
  selector: 'chronos-entity-select',
  imports: [
    NgbDropdownModule,
    FontAwesomeModule,
    FormsModule
  ],
  templateUrl: './entity-select.component.html',
  styleUrl: './entity-select.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EntitySelectComponent),
      multi: true
    }
  ]
})
export class EntitySelectComponent implements ControlValueAccessor {

  // Injected Depedencies
  protected allEntities = inject(SchemaService).allEntities().asReadonly();

  // Form control
  private onChange: (value?: EntityAO) => void = () => {};
  private onTouched: () => void = () => {};

  // Internals
  protected entity?: EntityAO;
  protected entitySearch = signal("");
  protected entities = computed(() => {
    const search = this.entitySearch().toLocaleLowerCase();
    return this.allEntities.value()?.filter(entity =>
      entity.key?.toLowerCase().includes(search)
    )
  });

  // ControlValueAccessor methods
  public writeValue(value: EntityAO): void {
    this.entity = value;
  }

  public registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // Custom Methods
  protected selectEntity(entity?: EntityAO): void {
    this.entity = entity;
    this.entitySearch.set("");
    this.onChange(entity);
    this.onTouched();
  }

}
