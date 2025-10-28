import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import { WikipediaArticleInfo } from 'src/app/model/wikipedia-article-info.model';

@Injectable({
  providedIn: 'root'
})
export class AdminWikiArticlesService {

  constructor(private http: HttpClient) { }

  public search(query: string, lang: string = 'en', offset?: number): Observable<Array<WikipediaArticleInfo>> {
    let params = new HttpParams();
    
    params = params.set("q", query);
    if (!!lang) {
      params = params.set('lang', lang)
    }
    if (!!offset) {
      params = params.set('offset', offset)
    }
    return this.http.get<Array<WikipediaArticleInfo>>("/api/admin/wiki/articles/search", { params })
  }

}
