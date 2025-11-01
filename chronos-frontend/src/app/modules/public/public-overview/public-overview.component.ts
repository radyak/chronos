import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { WikiArticlesService } from '../../admin/person/wiki-article.service';
import { Observable, of } from 'rxjs';
import { WikipediaSummary } from 'src/app/common/model/general/wikipedia/wikipedia-summary.model';

@Component({
  selector: 'chronos-public-overview',
  templateUrl: './public-overview.component.html',
  styleUrls: ['./public-overview.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    RouterModule,
    NgbTypeaheadModule,
    FormsModule,
    WikipediaSummaryComponent,
    FontAwesomeModule,
  ]
})
export class PublicOverviewComponent implements OnInit {

  wikipediaSummary$: Observable<WikipediaSummary> = of();

  constructor(private wikiArticlesService: WikiArticlesService) {

  }

  ngOnInit(): void {
    this. wikipediaSummary$ = this.wikiArticlesService.getRandomArticle(); //.subscribe(wikipediaSummary => this.wikipediaSummary = wikipediaSummary);
  }

}
