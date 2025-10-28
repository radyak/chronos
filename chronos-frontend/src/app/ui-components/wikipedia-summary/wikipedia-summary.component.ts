import {Component, Input} from '@angular/core';
import {WikipediaSummary} from "../../model/wikipedia-summary.model";
import {NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faInfoCircle} from "@fortawesome/free-solid-svg-icons";

@Component({
  standalone: true,
  selector: 'chronos-wikipedia-summary',
  templateUrl: './wikipedia-summary.component.html',
  imports: [
    NgOptimizedImage,
    NgIf,
    FontAwesomeModule,
    NgForOf
  ],
  styleUrls: ['./wikipedia-summary.component.scss']
})
export class WikipediaSummaryComponent {

  infoIcon = faInfoCircle;

  @Input()
  wikiSummary!: WikipediaSummary;
}
