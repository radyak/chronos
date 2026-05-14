import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {Observable} from "rxjs";
import { WikipediaSummary } from 'src/app/common/model/domain/wikipedia/wikipedia-summary.model';

@Injectable({
  providedIn: 'root'
})
export class WikiArticlesClient {

  constructor(private http: HttpClient) { }

  public getArticleByQid(qid: string, lang: string = 'en'): Observable<WikipediaSummary> {
    let params = new HttpParams();
    if (!!lang) {
      params = params.set('lang', lang)
    }
    return this.http.get<WikipediaSummary>(`/api/wiki/articles/${qid}`, { params })
  }

}
