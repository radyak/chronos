import {Component, OnInit} from '@angular/core';
import {EntriesService} from "../../service/entries.service";
import {Observable, of} from "rxjs";
import {Entry} from "../../../../model/entry.model";
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute} from "@angular/router";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {calculateMaxSpanningDateRange} from "../../../../util/date-range.utils";

@Component({
  selector: 'chronos-public-discover',
  templateUrl: './public-discover.component.html',
  styleUrls: ['./public-discover.component.scss']
})
export class PublicDiscoverComponent implements OnInit {

  searchIcon = faSearch;

  searchTerm: string = '';
  submittedSearchTerm: string = '';
  results$: Observable<Array<Entry>> = of([]);

  selectedEntry?: Entry;
  wikipediaSummary?: WikipediaSummary

  constructor(private entriesService: EntriesService,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(queryParams => {
      this.submittedSearchTerm = queryParams['search'];
      this.searchTerm = this.submittedSearchTerm || '';
      this.results$ = this.entriesService.find({
        title: this.submittedSearchTerm
      })
    });
  }

  selectEntry(entry: Entry) {
    if (this.selectedEntry?.id === entry.id) {
      return;
    } else {
      this.selectedEntry = entry;
    }

    if (!entry.id) {
      delete this.wikipediaSummary;
      return
    }
    this.entriesService.getWikipediaSummary(entry.id).subscribe(wikipediaSummary => {
      this.wikipediaSummary = wikipediaSummary
    })
  }

  protected readonly calculateMaxSpanningDateRange = calculateMaxSpanningDateRange;
}
