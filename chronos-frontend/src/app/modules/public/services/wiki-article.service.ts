import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { QueryDTO } from 'src/app/common/model/data/query.model.dto';
import { EntryDTO } from 'src/app/common/model/data/data-element.dto';
import { CountResultDTO } from 'src/app/common/model/data/count-result.dto';
import { rxResource } from '@angular/core/rxjs-interop';
import { HistoricalDataClient } from '../clients/historical-data.client';
import { WikiArticlesClient } from '../clients/wiki-article.client';
import { switchMap } from 'rxjs';
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';

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
        return this.historicalDataClient.search({
          sortBy: 'random',
          pageSize: 1,
        }, {
          "wikiqid:not": "null"
        }).pipe(
            switchMap(response => {
              const entity = response.entries[0];
              return this.wikiArticlesClient.getArticleByQid(entity.properties["wikiqid"]!)
            })
        );
      },
    });
  }
  
}
