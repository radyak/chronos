import { Component, computed, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminIconsService } from 'src/app/modules/admin/services/admin-icons.service';

@Component({
  selector: 'chronos-icon-select',
  imports: [
    NgbDropdownModule,
    FontAwesomeModule,
    FormsModule
  ],
  templateUrl: './icon-select.component.html',
  styleUrl: './icon-select.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => IconSelectComponent),
      multi: true
    }
  ]
})
export class IconSelectComponent implements ControlValueAccessor {

  // Injected Depedencies
  private adminIconsService = inject(AdminIconsService);

  // Form control
  private onChange: (value?: string) => void = () => {};
  private onTouched: () => void = () => {};

  // Internals
  protected icon?: string;
  protected iconSearch = signal("");
  protected iconNames = computed(() => {
    const search = this.iconSearch().toLocaleLowerCase();
    return this.adminIconsService.iconNames.filter(icon =>
      icon.toLowerCase().includes(search)
    )
  });

  // ControlValueAccessor methods
  public writeValue(value: string): void {
    this.icon = value;
  }

  public registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  public registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // Custom Methods
  protected selectIcon(icon?: string): void {
    this.icon = icon;
    this.iconSearch.set("");
    this.onChange(icon);
    this.onTouched();
  }

}
