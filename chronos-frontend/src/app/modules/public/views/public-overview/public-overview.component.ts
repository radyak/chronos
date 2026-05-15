import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { Observable, of } from 'rxjs';
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';
import { WikiArticleService } from '../../services/wiki-article.service';
import { LoadingComponent } from 'src/app/common/components/loading/loading.component';

@Component({
    selector: 'chronos-public-overview',
    templateUrl: './public-overview.component.html',
    styleUrls: ['./public-overview.component.scss'],
    imports: [
        CommonModule,
        RouterModule,
        NgbTypeaheadModule,
        FormsModule,
        WikipediaSummaryComponent,
        FontAwesomeModule,
        LoadingComponent
    ]
})
export class PublicOverviewComponent {

  // Injected dependencies
  private wikiArticlesService = inject(WikiArticleService);

  // Derived signals
  protected randomWikiArticle = this.wikiArticlesService.randomWikiArticle();

}
