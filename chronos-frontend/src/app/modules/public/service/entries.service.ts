import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Entry} from "../../../model/entry.model";
import {WikipediaSummary} from "../../../model/wikipedia-summary.model";

export interface EntriesSearchParams {
  title?: string;
  tagIds?: Array<number>;
  from?: number;
  to?: number;
  ids?: Array<number>;
}

@Injectable({
  providedIn: null
})
export class EntriesService {

  constructor(private http: HttpClient) { }

  public allEntries(): Observable<Array<Entry>> {
    return this.http.get<Array<Entry>>("/api/entries")
  }

  public find(entriesSearchParams: EntriesSearchParams): Observable<Array<Entry>> {
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
    return this.http.get<Array<Entry>>("/api/entries", { params })
  }

  public getWikipediaSummary(entryId: number): Observable<WikipediaSummary> {
    return this.http.get<WikipediaSummary>(`/api/entries/${entryId}/wikipediasummary`);
  }

  public getRandomWikipediaSummary(): Observable<WikipediaSummary> {
    return this.http.get<WikipediaSummary>(`/api/entries/random/wikipediasummary`);
  }

}
