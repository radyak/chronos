import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { QueryDTO } from 'src/app/common/model/data/query.model.dto';
import { EntryDTO } from 'src/app/common/model/data/data-element.dto';
import { CountResultDTO } from 'src/app/common/model/data/count-result.dto';
import { rxResource } from '@angular/core/rxjs-interop';
import { HistoricalDataClient } from '../clients/historical-data.client';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataService {

  // Injected Dependencies
  private historicalDataClient = inject(HistoricalDataClient);
  
  public search(query: Signal<QueryDTO>): ResourceRef<EntryDTO[] | undefined> {
    return rxResource({
      params: () => query(),
      stream: ({ params }) => {
        return this.historicalDataClient.search(params);
      },
    });
  }
  
  public statistics(): ResourceRef<CountResultDTO[] | undefined> {
    return rxResource({
      stream: () => {
        return this.historicalDataClient.getStatistics();
      },
    });
  }
  
}
