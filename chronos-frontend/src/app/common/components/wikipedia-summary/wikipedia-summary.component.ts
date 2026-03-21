import {Component, Input} from '@angular/core';

import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faInfoCircle} from "@fortawesome/free-solid-svg-icons";
import { WikipediaSummary } from 'src/app/common/model/general/wikipedia/wikipedia-summary.model';

@Component({
    selector: 'chronos-wikipedia-summary',
    templateUrl: './wikipedia-summary.component.html',
    imports: [
    FontAwesomeModule
],
    styleUrls: ['./wikipedia-summary.component.scss']
})
export class WikipediaSummaryComponent {

  infoIcon = faInfoCircle;

  @Input()
  wikiSummary!: WikipediaSummary;
}
