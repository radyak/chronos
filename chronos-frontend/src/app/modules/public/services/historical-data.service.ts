import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { QueryDTO } from 'src/app/common/model/data/query.model.dto';
import { EntryDTO } from 'src/app/common/model/data/entry.dto';
import { CountResultDTO } from 'src/app/common/model/data/count-result.dto';
import { rxResource } from '@angular/core/rxjs-interop';
import { HistoricalDataClient } from '../clients/historical-data.client';
import { map, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataService {

  // Injected Dependencies
  private historicalDataClient = inject(HistoricalDataClient);
  
  public list(query: Signal<QueryDTO>): ResourceRef<EntryDTO[] | undefined> {
    return rxResource({
      params: () => query(),
      stream: ({ params }) => {
        return this.historicalDataClient.list(params).pipe(
          // Map the DataResponseDTO to just the entries for easier consumption
          map(response => {
            return response.entries;
          })
        );
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
  
  public entry(key: Signal<string>): ResourceRef<EntryDTO | undefined> {
    return rxResource({
      params: () => key(),
      stream: ({ params }) => {
        return !!params ? this.historicalDataClient.getEntry(params) : of(undefined);
      },
    });
  }
  
}
