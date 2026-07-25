import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { EntryDTO } from 'src/app/common/model/data/response/entry.dto';

@Injectable({
  providedIn: 'root',
})
export class AdminDataClient {
  private http = inject(HttpClient);
  private adminApiUrl = '/api/data/admin';
  
  public saveEntry(entry: EntryDTO): Observable<EntryDTO> {
    if (entry.elementId) {
      return this.http.put<EntryDTO>(`${this.adminApiUrl}/${entry.attributes['key']}`, entry).pipe(take(1));
    } else {
      return this.http.post<EntryDTO>(`${this.adminApiUrl}`, entry).pipe(take(1));
    }
  }

  public deleteEntry(entryKey: string): Observable<void> {
    if (entryKey) {
      return this.http.delete<void>(`${this.adminApiUrl}/${entryKey}`).pipe(take(1));
    }
    return of()
  }
}
