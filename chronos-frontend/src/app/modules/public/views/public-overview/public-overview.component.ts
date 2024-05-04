import {Component, OnInit} from '@angular/core';
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {EntriesService} from "../../service/entries.service";
import {Observable} from "rxjs";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";

@Component({
  selector: 'chronos-public-overview',
  templateUrl: './public-overview.component.html',
  styleUrls: ['./public-overview.component.scss']
})
export class PublicOverviewComponent implements OnInit {

  wikipediaSummary!: WikipediaSummary;

  constructor(private entriesService: EntriesService) {

  }

  ngOnInit(): void {
    this.entriesService.getRandomWikipediaSummary()
      .subscribe(wikipediaSummary => this.wikipediaSummary = wikipediaSummary);
  }

}
