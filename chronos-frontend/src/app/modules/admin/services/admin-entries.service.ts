import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, MonoTypeOperatorFunction, Observable, of, tap} from "rxjs";
import {Entry} from "../../../model/entry.model";
import {NotificationService} from "../../../ui-components/notifications/notification.service";
import {WikipediaSummary} from "../../../model/wikipedia-summary.model";
import {EntriesSearchParams} from "../../public/service/entries.service";

@Injectable({
  providedIn: null
})
export class AdminEntriesService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public allEntries(entriesSearchParams: EntriesSearchParams): Observable<Array<Entry>> {
    let params = new HttpParams();
    if (entriesSearchParams.title) {
      params = params.set('title', entriesSearchParams.title)
    }
    if (entriesSearchParams.tagIds && entriesSearchParams.tagIds.length > 0) {
      params = params.set('tags', entriesSearchParams.tagIds.join(','))
    }
    if (entriesSearchParams.from) {
      params = params.set('from', entriesSearchParams.from)
    }
    if (entriesSearchParams.to) {
      params = params.set('to', entriesSearchParams.to)
    }
    if (entriesSearchParams.ids) {
      params = params.set('ids', entriesSearchParams.ids.join(','))
    }
    return this.http.get<Array<Entry>>("/api/admin/entries", { params })
  }

  public getEntry(id: number): Observable<Entry> {
    return this.http.get<Entry>(`/api/admin/entries/${id}`);
  }

  public enrichEntry(entry: Entry): void {
    const relationIds = new Set<number>();
    entry.relations?.forEach(relation => {
      relationIds.add(relation.fromId);
      relationIds.add(relation.toId);
    });
    this.allEntries({
      ids: [...relationIds]
    }).subscribe(entries => {
      const findById = (id: number) => entries.find(e => e.id === id);
      entry.relations?.forEach(relation => {
        relation.from = findById(relation.fromId);
        relation.to = findById(relation.toId);
      });
    })
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
