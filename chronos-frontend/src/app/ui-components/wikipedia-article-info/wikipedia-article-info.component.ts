import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";
import { WikipediaArticleInfo } from 'src/app/model/wikipedia-article-info.model';

@Component({
  selector: 'chronos-wikipedia-article-info',
  templateUrl: './wikipedia-article-info.component.html',
  styleUrls: ['./wikipedia-article-info.component.scss'],
  standalone: true,
  imports: [
    NgIf,
  ],
})
export class WikipediaArticleInfoComponent {

  @Input("article")
  public article!: WikipediaArticleInfo;

}
