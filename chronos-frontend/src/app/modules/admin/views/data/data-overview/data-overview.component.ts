import { Component, computed, inject, signal, WritableSignal } from '@angular/core';
import { HistoricalDataService } from '../../../services/historical-data.service';
import { QueryDTO } from 'src/app/common/model/domain/data/query.model.dto';
import { ElementAttributePipe } from 'src/app/common/util/element-attribute.pipe';
import { FormsModule } from '@angular/forms';

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

  protected query: WritableSignal<QueryDTO> = signal({
    pageSize: 10
  });

  // Injected Dependencies
  protected historicalDataService = inject(HistoricalDataService);

  // Derived Signals
  protected data = this.historicalDataService.search(this.query);
  protected statistics = this.historicalDataService.statistics();
  protected total = computed(() => {
    const stats = this.statistics.value();
    return stats ? stats.reduce((sum, { count }) => sum + count, 0) : 0;
  });
  protected hasPreviousPage = computed(() => 
    this.query().page !== undefined && this.query().page! > 0
  );
  protected hasNextPage = computed(() => {
    const dataLength = this.data.value()?.length;
    const pageSize = this.query().pageSize;
    return (dataLength === pageSize);
  }
  );

  // Methods
  protected setPageSize(size: number): void {
    if (typeof size === 'string') {
      size = parseInt(size);
    }
    this.query.update(query => ({
      ...query,
      pageSize: size,
      page: 0 // Reset to first page when page size changes
    }));
  }

  protected previousPage(): void {
    this.pageStep(-1);
  }

  protected nextPage(): void {
    this.pageStep(1);
  }

  private pageStep(step: number): void {
    this.query.update(query => ({
        ...query,
        page: (query.page || 0) + step
    }));
  }

}
