import {Component} from '@angular/core';
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {FormsModule} from "@angular/forms";
import {Params, Router} from "@angular/router";

@Component({
  standalone: true,
  selector: 'chronos-entries-search-input',
  templateUrl: './entries-search-input.component.html',
  imports: [
    FontAwesomeModule,
    FormsModule
  ],
  styleUrls: ['./entries-search-input.component.scss']
})
export class EntriesSearchInputComponent {

  protected searchTerm: string = '';
  protected readonly searchIcon = faSearch;

  constructor(private router: Router) {
  }

  search(): void {
    const queryParams: Params = {
      'search': this.searchTerm || null
    };
    this.router.navigate(
      // TODO: This depends on the actual routing config; should be extracted
      ["/discover"],
      {
        queryParams,
      }
    );
  }

  protected keyDown(event: any) {
    if (event.key === 'Enter') {
      this.search();
    }
  }

}
