import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { of, switchMap } from 'rxjs';
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';
import { HistoricalDataClient } from '../clients/historical-data.client';
import { WikiArticlesClient } from '../clients/wiki-article.client';

@Injectable({
  providedIn: 'root',
})
export class WikiArticleService {

  // Injected Dependencies
  private historicalDataClient = inject(HistoricalDataClient);
  private wikiArticlesClient = inject(WikiArticlesClient);
  
  public randomWikiArticle(): ResourceRef<WikipediaSummary | undefined> {
    return rxResource({
      stream: () => {
        return this.historicalDataClient.list({
          sortBy: 'random',
          pageSize: 1,
        }, {
          "wikiqid:not": "null"
        }).pipe(
            switchMap(response => {
              const entity = response.entries[0];
              return this.wikiArticlesClient.getArticleByQid(entity.attributes["wikiqid"]!)
            })
        );
      },
    });
  }

  public getArticle(qid: Signal<string | undefined>): ResourceRef<WikipediaSummary | undefined> {
    return rxResource({
      params: () => qid(),
      stream: ({ params }) => params ? this.wikiArticlesClient.getArticleByQid(params) : of(undefined),
    });
  }
  
}
