import { Component, computed, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { TypeAO } from 'src/app/common/model/schema/admin/type.ao';
import { AdminSchemaService } from 'src/app/modules/admin/services/admin-schema.service';

@Component({
  selector: 'chronos-type-select',
  imports: [
    NgbDropdownModule,
    FontAwesomeModule,
    FormsModule
  ],
  templateUrl: './type-select.component.html',
  styleUrl: './type-select.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TypeSelectComponent),
      multi: true
    }
  ]
})
export class TypeSelectComponent implements ControlValueAccessor {

  // Injected Depedencies
  protected allTypes = inject(AdminSchemaService).allTypes().asReadonly();

  // Form control
  private onChange: (value?: TypeAO) => void = () => {};
  private onTouched: () => void = () => {};

  // Internals
  protected type?: TypeAO;
  protected typeSearch = signal("");
  protected types = computed(() => {
    const search = this.typeSearch().toLocaleLowerCase();
    return this.allTypes.value()?.filter(type =>
      type.key?.toLowerCase().includes(search)
    )
  });

  // ControlValueAccessor methods
  public writeValue(value: TypeAO): void {
    this.type = value;
  }

  public registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // Custom Methods
  protected selectType(type?: TypeAO): void {
    this.type = type;
    this.typeSearch.set("");
    this.onChange(type);
    this.onTouched();
  }

}
