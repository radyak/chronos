import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import { WikipediaSummary } from 'src/app/model/wikipedia-summary.model';

export interface PersonSearchParams {
  from?: number;
  to?: number;
}

@Injectable({
  providedIn: 'root'
})
export class WikiArticlesService {

  constructor(private http: HttpClient) { }

  public getArticleByQid(qid: string, lang: string = 'en'): Observable<WikipediaSummary> {
    let params = new HttpParams();
    if (!!lang) {
      params = params.set('lang', lang)
    }
    return this.http.get<WikipediaSummary>(`/api/wiki/articles/${qid}`, { params })
  }

  public getRandomArticle(lang: string = 'en'): Observable<WikipediaSummary> {
    let params = new HttpParams();
    if (!!lang) {
      params = params.set('lang', lang)
    }
    return this.http.get<WikipediaSummary>(`/api/wiki/articles/random`, { params })
  }

}
