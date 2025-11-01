import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";
import { WikipediaArticleInfo } from 'src/app/common/model/general/wikipedia/wikipedia-article-info.model';

@Component({
  standalone: true,
  selector: 'chronos-wikipedia-article-info',
  templateUrl: './wikipedia-article-info.component.html',
  styleUrls: ['./wikipedia-article-info.component.scss'],
  imports: [
    NgIf,
  ],
})
export class WikipediaArticleInfoComponent {

  @Input("article")
  public article!: WikipediaArticleInfo;

}
