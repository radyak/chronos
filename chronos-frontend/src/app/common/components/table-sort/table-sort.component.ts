import { Component, HostBinding, HostListener, input, output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { SortOrder } from '../../model/domain/data/sort-order.dto';

interface SortState {
  sortBy?: string;
  sortOrder?: SortOrder;
}


@Component({
  selector: '[table-sort]',
  imports: [
    FontAwesomeModule
  ],
  templateUrl: './table-sort.component.html',
  styleUrl: './table-sort.component.scss',
})
export class TableSortComponent {

  // Inputs
  sort = input.required<SortState>();
  field = input.required<string>({
    alias: 'table-sort'
  });
  onSort = output<string>();

  // Icons
  protected readonly faSort = faSort;
  protected readonly faSortUp = faSortUp;
  protected readonly faSortDown = faSortDown;


  @HostBinding('style.cursor')
  cursor = 'pointer';

  @HostListener('click')
  toggleSort(): void {
    this.onSort.emit(this.field());
  }

}
