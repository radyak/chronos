import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { EntryDTO } from 'src/app/common/model/data/entry.dto';

@Injectable({
  providedIn: 'root',
})
export class AdminDataClient {
  private http = inject(HttpClient);
  private adminApiUrl = '/api/data/admin';
  
  public saveEntry(entry: EntryDTO): Observable<void> {
    if (entry.elementId) {
      return this.http.put<void>(`${this.adminApiUrl}/${entry.attributes['key']}`, entry).pipe(take(1));
    } else {
      return this.http.post<void>(`${this.adminApiUrl}`, entry).pipe(take(1));
    }
  }

  public deleteEntry(entry: EntryDTO): Observable<void> {
    if (entry.attributes['key']) {
      return this.http.delete<void>(`${this.adminApiUrl}/${entry.attributes['key']}`).pipe(take(1));
    }
    return of()
  }
}
