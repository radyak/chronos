import { Component, Input } from '@angular/core';
import { WikipediaSummary } from 'src/app/common/model/wikipedia/wikipedia-summary.model';

@Component({
    selector: 'chronos-wikipedia-summary-core',
    templateUrl: './wikipedia-summary-core.component.html',
    imports: [
    ],
    styleUrls: ['./wikipedia-summary-core.component.scss']
})
export class WikipediaSummaryCoreComponent {

  @Input()
  wikiSummary!: WikipediaSummary;

}
