import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CountResultDTO } from 'src/app/common/model/data/count-result.dto';
import { EntryDTO } from 'src/app/common/model/data/data-element.dto';
import { DataResponseDTO } from 'src/app/common/model/data/data-response.dto';
import { QueryDTO } from 'src/app/common/model/data/query.model.dto';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/data';
  

  public search(query?: QueryDTO, filters?: Record<string, string>): Observable<DataResponseDTO> {
    return this.http.get<DataResponseDTO>(`${this.apiUrl}`, { params: {...query, ...filters} as any });
  }

  public getStatistics(): Observable<CountResultDTO[]> {
    return this.http.get<CountResultDTO[]>(`${this.apiUrl}/statistics`);
  }

}
