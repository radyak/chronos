import { NgClass } from '@angular/common';
import {Component, Input} from '@angular/core';

import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faInfoCircle} from "@fortawesome/free-solid-svg-icons";
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';
import { WikipediaSummaryCoreComponent } from '../wikipedia-summary-core/wikipedia-summary-core.component';

@Component({
    selector: 'chronos-wikipedia-summary',
    templateUrl: './wikipedia-summary.component.html',
    imports: [
        FontAwesomeModule,
        NgClass,
        WikipediaSummaryCoreComponent
    ],
    styleUrls: ['./wikipedia-summary.component.scss']
})
export class WikipediaSummaryComponent {

  infoIcon = faInfoCircle;

  @Input()
  wikiSummary!: WikipediaSummary;

}
