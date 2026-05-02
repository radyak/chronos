import { inject, Injectable, resource, ResourceRef, Signal } from '@angular/core';
import { HistoricalDataClient } from './historical-data.client';
import { QueryDTO } from 'src/app/common/model/domain/data/query.model.dto';
import { DataElementDTO } from 'src/app/common/model/domain/data/data-element.dto';
import { firstValueFrom } from 'rxjs';
import { CountResultDTO } from 'src/app/common/model/domain/data/count-result.dto';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataService {

  // Injected Dependencies
  private historicalDataClient = inject(HistoricalDataClient);
  
  public search(query: Signal<QueryDTO>): ResourceRef<DataElementDTO[] | undefined> {
    return resource({
      params: () => query(),
      loader: async ({ params }) => {
        return await firstValueFrom(this.historicalDataClient.search(params));
      },
    });
  }
  
  public statistics(): ResourceRef<CountResultDTO[] | undefined> {
    return resource({
      loader: async () => {
        return await firstValueFrom(this.historicalDataClient.getStatistics());
      },
    });
  }
  
}
