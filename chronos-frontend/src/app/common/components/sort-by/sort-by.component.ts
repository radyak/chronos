import { Component, HostBinding, HostListener, input, output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { SortOrder } from '../../model/data/sort-order.dto';

interface SortState {
  sortBy?: string;
  sortOrder?: SortOrder;
}


@Component({
  selector: '[sortBy]',
  imports: [
    FontAwesomeModule
  ],
  templateUrl: './sort-by.component.html',
  styleUrl: './sort-by.component.scss',
})
export class SortByComponent {

  // Inputs
  sort = input.required<SortState>();
  field = input.required<string>({
    alias: 'sortBy'
  });
  onSortBy = output<string>();

  // Icons
  protected readonly faSort = faSort;
  protected readonly faSortUp = faSortUp;
  protected readonly faSortDown = faSortDown;


  @HostBinding('style.cursor')
  cursor = 'pointer';

  @HostListener('click')
  toggleSort(): void {
    this.onSortBy.emit(this.field());
  }

}
