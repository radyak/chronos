import { inject, Injectable, ResourceRef, Signal } from '@angular/core';
import { NotificationService } from 'src/app/common/components/notifications/notification.service';
import { rxResource } from '@angular/core/rxjs-interop';
import { AdminWikiArticlesClient } from '../clients/admin-wiki-article.client';
import { WikipediaArticleInfo } from 'src/app/common/model/wikipedia/wikipedia-article-info.model';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminWikiService {

  // Injected Dependencies
  private adminWikiArticlesClient = inject(AdminWikiArticlesClient);
  private notificationService = inject(NotificationService);

  public articleSearch(search: Signal<string>): ResourceRef<WikipediaArticleInfo[] | undefined> {
    return rxResource({
      params: () => search(),
      stream: ({ params }) => {
        console.log('searching for:', params);
        if (!params || params.length <= 3) {
          return of([]);
        }
        return this.adminWikiArticlesClient.search(params)
      }
    });
  }

}
