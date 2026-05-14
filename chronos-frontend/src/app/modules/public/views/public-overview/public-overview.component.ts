import { CommonModule } from '@angular/common';
import {} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { filter, Observable, of, switchMap } from 'rxjs';
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';
import { WikiArticlesClient } from '../../clients/wiki-article.client';
import { EntitiesClient } from '../../clients/entities.client';

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
    ]
})
export class PublicOverviewComponent implements OnInit {

  // Injected dependencies
  private wikiArticlesService = inject(WikiArticlesClient);
  private entitiesClient = inject(EntitiesClient);

  // Local state
  wikipediaSummary$: Observable<WikipediaSummary> = of();

  constructor(
  ) { }

  ngOnInit(): void {
    this.wikipediaSummary$ = this.entitiesClient.getRandomEntityWithQid$().pipe(
      filter(entity => !!entity.qid),
      switchMap(entitiy => this.wikiArticlesService.getArticleByQid(entitiy.qid!))
    );
  }

}
