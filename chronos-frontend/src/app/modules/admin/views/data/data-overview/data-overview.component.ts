import { Component, computed, inject, Signal } from '@angular/core';
import { HistoricalDataService } from '../../../services/historical-data.service';
import { QueryDTO } from 'src/app/common/model/domain/data/query.model.dto';
import { ElementAttributePipe } from 'src/app/common/util/element-attribute.pipe';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

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
  ],
  templateUrl: './data-overview.component.html',
  styleUrl: './data-overview.component.scss',
})
export class DataOverviewComponent {

  // Injected Dependencies
  protected historicalDataService = inject(HistoricalDataService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // Derived Signals
  protected queryParams: Signal<QueryDTO> = toSignal(
    this.route.queryParamMap.pipe(
      map(params => ({
        page: Number(params.get('page') ?? 1),
        pageSize: Number(params.get('pageSize') ?? 10),
        // TODO: Add sorting
      }))
    ),
    { initialValue: { page: 1, pageSize: 10 } }
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

}
