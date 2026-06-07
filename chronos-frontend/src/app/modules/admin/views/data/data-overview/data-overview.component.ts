import { Component, computed, inject, Signal } from '@angular/core';
import { HistoricalDataService } from '../../../../public/services/historical-data.service';
import { QueryDTO } from 'src/app/common/model/data/query.model.dto';
import { ElementAttributePipe } from 'src/app/common/util/element-attribute.pipe';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { firstValueFrom, map } from 'rxjs';
import { SortByComponent } from 'src/app/common/components/sort-by/sort-by.component';
import { SortOrder } from 'src/app/common/model/data/sort-order.dto';
import { LoadingComponent } from 'src/app/common/components/loading/loading.component';
import { IconConstants } from 'src/app/common/constants/icon.constants';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CREATE_ROUTE_KEYWORD } from '../../../admin.routes';
import { EntryDTO } from 'src/app/common/model/data/entry.dto';
import { AdminDataClient } from '../../../clients/admin-data.client';
import { AdminDataService } from '../../../services/admin-data.service';

function cleanParams(obj: Record<string, any>) {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null)
  );
}

@Component({
  selector: 'chronos-data-overview',
  imports: [
    ElementAttributePipe,
    FormsModule,
    SortByComponent,
    LoadingComponent,
    FontAwesomeModule
  ],
  templateUrl: './data-overview.component.html',
  styleUrl: './data-overview.component.scss',
})
export class DataOverviewComponent {

  protected newIcon = IconConstants.ICON_ADD;
  protected editIcon = IconConstants.ICON_EDIT;
  protected deleteIcon = IconConstants.ICON_DELETE;

  // Injected Dependencies
  protected historicalDataService = inject(HistoricalDataService);
  protected adminDataService = inject(AdminDataService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Derived Signals
  protected queryParams: Signal<QueryDTO> = toSignal(
    this.route.queryParamMap.pipe(
      map(params => {
        const sortOrderString: string | null = params.get('sortOrder');
        const sortOrder: SortOrder = sortOrderString ? SortOrder[sortOrderString.toUpperCase() as keyof typeof SortOrder] : SortOrder.ASC;
        return {
          page: Number(params.get('page') ?? 1),
          pageSize: Number(params.get('pageSize') ?? 10),
          sortOrder: sortOrder,
          sortBy: params.get('sortBy') ?? undefined,
        }
    })
    ),
    { initialValue: {
      page: 1,
      pageSize: 10,
      sortOrder: SortOrder.ASC,
      sortBy: undefined,
    } }
  );
  protected data = this.historicalDataService.search(this.queryParams);
  protected statistics = this.historicalDataService.statistics();
  protected total = computed(() => {
    const stats = this.statistics.value();
    return stats ? stats.reduce((sum, { count }) => sum + count, 0) : 0;
  });
  protected hasPreviousPage = computed(() => 
    this.queryParams().page !== undefined && this.queryParams().page! > 1
  );
  protected hasNextPage = computed(() => {
    const dataLength = this.data.value()?.length;
    const pageSize = this.queryParams().pageSize;
    return (dataLength === pageSize);
  });

  // Methods
  protected setPageSize(size: number): void {
    if (typeof size === 'string') {
      size = parseInt(size);
    }
    this.updateQuery({
      pageSize: size,
      page: 1 // Reset to first page when page size changes
    });
  }

  protected previousPage(): void {
    this.pageStep(-1);
  }

  protected nextPage(): void {
    this.pageStep(1);
  }

  private pageStep(step: number): void {
    const page = Math.max(1, (this.queryParams().page ?? 1) + step);
    this.updateQuery({
        page: page
    });
  }

  private updateQuery(patch: Partial<QueryDTO>) {
    const current = this.queryParams();

    const next = {
      ...current,
      ...patch,
    };

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: cleanParams(next),
      replaceUrl: true,
    });
  }

  protected sortBy(field: string): void {
    const current = this.queryParams();
    this.updateQuery({
      sortBy: field,
      sortOrder: current.sortBy === field ? (current.sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC) : SortOrder.ASC,
      page: 1
    });
  }

  protected newEntry(): void {
      this.router.navigate([CREATE_ROUTE_KEYWORD], { relativeTo: this.route });
  }

  protected editEntry(entry: EntryDTO): void {
    const key = entry.attributes['key'];
    this.router.navigate([key], { relativeTo: this.route });
  }

  protected deleteEntry(entry: EntryDTO): void {
      firstValueFrom(this.adminDataService.delete(entry)).then(
        () => {
          this.updateQuery({})
        }
      );
  }

}
