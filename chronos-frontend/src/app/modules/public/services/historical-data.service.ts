import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { ListQueryDTO } from 'src/app/common/model/data/query/list-query.model.dto';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';
import { CountResultDTO } from 'src/app/common/model/data/response/count-result.dto';
import { rxResource } from '@angular/core/rxjs-interop';
import { HistoricalDataClient } from '../clients/historical-data.client';
import { map, of } from 'rxjs';
import { MeshQueryDTO } from 'src/app/common/model/data/query/mesh-query.model.dto';
import { DataResponseDTO } from 'src/app/common/model/data/response/data-response.dto';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataService {

  // Injected Dependencies
  private historicalDataClient = inject(HistoricalDataClient);
  
  public list(query: Signal<ListQueryDTO>): ResourceRef<EntryDTO[] | undefined> {
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
  
  public mesh(query: Signal<MeshQueryDTO>): ResourceRef<DataResponseDTO | undefined> {
    return rxResource({
      params: () => query(),
      stream: ({ params }) => {
        return this.historicalDataClient.mesh(params);
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
