import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, take } from 'rxjs';
import { CountResultDTO } from 'src/app/common/model/data/response/count-result.dto';
import { DataResponseDTO } from 'src/app/common/model/data/response/data-response.dto';
import { ListQueryDTO } from 'src/app/common/model/data/query/list-query.model.dto';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';
import { MeshQueryDTO } from 'src/app/common/model/data/query/mesh-query.model.dto';

@Injectable({
  providedIn: 'root',
})
export class HistoricalDataClient {
  private http = inject(HttpClient);
  private apiUrl = '/api/data';
  

  public list(query?: ListQueryDTO, filters?: Record<string, string>): Observable<DataResponseDTO> {
    return this.http.get<DataResponseDTO>(`${this.apiUrl}/list`, { params: {...query, ...filters} as any });
  }

  public mesh(query?: MeshQueryDTO): Observable<DataResponseDTO> {
    return this.http.post<DataResponseDTO>(`${this.apiUrl}/mesh`, query).pipe(take(1));
  }

  public getStatistics(): Observable<CountResultDTO[]> {
    return this.http.get<CountResultDTO[]>(`${this.apiUrl}/statistics`);
  }

  public getEntry(key: string): Observable<EntryDTO> {
    return this.http.get<EntryDTO>(`${this.apiUrl}/${key}`);
  }

}
