import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, MonoTypeOperatorFunction, Observable, of, tap} from "rxjs";
import {Entry} from "../../../model/entry.model";
import {NotificationService} from "../../../ui-components/notifications/notification.service";
import {WikipediaSummary} from "../../../model/wikipedia-summary.model";

@Injectable({
  providedIn: null
})
export class AdminEntriesService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public allEntries(): Observable<Array<Entry>> {
    return this.http.get<Array<Entry>>("/api/admin/entries")
  }

  public getEntry(id: number): Observable<Entry> {
    return this.http.get<Entry>(`/api/admin/entries/${id}`);
  }

  public saveEntry(entry: Entry): Observable<Entry> {
    if (entry.id === undefined) {
      return this.createEntry(entry);
    } else {
      return this.updateEntry(entry);
    }
  }

  public deleteEntry(entry: Entry): Observable<void> {
    return this.http.delete<void>(`/api/admin/entries/${entry.id}`).pipe(
      this.successNotification(`Deleted entry "${entry.title}"`)
    );
  }

  public findWikipediaSummary(searchTitle: string): Observable<WikipediaSummary | undefined> {
    let params = new HttpParams();
    params = params.set('title', searchTitle);
    return this.http.get<WikipediaSummary>(`/api/admin/entries/wikipediasummary`, { params }).pipe(
      catchError(e => {
        return of(undefined)
      })
    );
  }

  private createEntry(entry: Entry): Observable<Entry> {
    return this.http.post<Entry>("/api/admin/entries", entry).pipe(
      this.successNotification(`Created entry "${entry.title}"`)
    );
  }

  private updateEntry(entry: Entry): Observable<Entry> {
    return this.http.put<Entry>("/api/admin/entries", entry).pipe(
      this.successNotification(`Updated entry "${entry.title}"`)
    );
  }

  private successNotification(text: string): MonoTypeOperatorFunction<any> {
    return tap({
      next: () => this.notificationService.success(text)
    })
  }

}
