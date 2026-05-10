import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CountResultDTO } from 'src/app/common/model/domain/data/count-result.dto';
import { DataElementDTO } from 'src/app/common/model/domain/data/data-element.dto';
import { QueryDTO } from 'src/app/common/model/domain/data/query.model.dto';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/data';
  

  public search(query?: QueryDTO): Observable<DataElementDTO[]> {
    return this.http.get<DataElementDTO[]>(`${this.apiUrl}/entries`, { params: query as any });
  }

  public getStatistics(): Observable<CountResultDTO[]> {
    return this.http.get<CountResultDTO[]>(`${this.apiUrl}/entries/statistics`);
  }

}
