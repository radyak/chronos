import {Component, Input} from '@angular/core';
import { NgStyle } from "@angular/common";
import { WikipediaArticleInfo } from 'src/app/common/model/wikipedia/wikipedia-article-info.model';
import { NgbAccordionModule } from "@ng-bootstrap/ng-bootstrap";
import { faQuestion } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from "@fortawesome/angular-fontawesome";

@Component({
    selector: 'chronos-wikipedia-article-info',
    templateUrl: './wikipedia-article-info.component.html',
    styleUrls: ['./wikipedia-article-info.component.scss'],
    imports: [
    NgbAccordionModule,
    FontAwesomeModule,
    NgStyle
]
})
export class WikipediaArticleInfoComponent {

  @Input("article")
  public article!: WikipediaArticleInfo;

  noImageIcon = faQuestion;

  protected getStyle() {
    return {
      'background-image': `url(${this.article.image?.url})`
    }
  }

}
