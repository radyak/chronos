import { Component, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { WikipediaArticleInfoComponent } from '../wikipedia-article-info/wikipedia-article-info.component';
import { WikipediaArticleInfo } from '../../model/wikipedia/wikipedia-article-info.model';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { IconConstants } from '../../constants/icon.constants';
import { NgbTypeahead } from '@ng-bootstrap/ng-bootstrap';
import { debounceTime, map, Observable, of, OperatorFunction, switchMap, tap } from 'rxjs';
import { AdminWikiArticlesClient } from 'src/app/modules/admin/clients/admin-wiki-article.client';
import { WikipediaSummaryComponent } from '../wikipedia-summary/wikipedia-summary.component';
import { WikiArticleService } from 'src/app/modules/public/services/wiki-article.service';
import { WikipediaSummaryCoreComponent } from "../wikipedia-summary-core/wikipedia-summary-core.component";

@Component({
  selector: 'chronos-wiki-article-input',
  imports: [
    FormsModule,
    WikipediaArticleInfoComponent,
    FaIconComponent,
    NgbTypeahead,
    WikipediaSummaryCoreComponent
],
  templateUrl: './wiki-article-input.component.html',
  styleUrl: './wiki-article-input.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => WikiArticleInputComponent),
      multi: true
    }
  ]
})
export class WikiArticleInputComponent implements ControlValueAccessor {

  // Properties
  protected search = signal('');
  protected _selectedArticle: Partial<WikipediaArticleInfo> = {};
  protected isLoading = signal(false);
  protected qid = signal<string | undefined>(undefined);

  // Injected Dependencies
  private adminWikiArticlesClient = inject(AdminWikiArticlesClient);
  protected loadedArticle = inject(WikiArticleService).getArticle(this.qid);

  // Form Control
  public disabled = signal(false);
  private formControlOnChange: Function = () => {};
  private formControlOnTouched: Function = () => {};

  // Typeahead
  protected onSearch: OperatorFunction<string, readonly WikipediaArticleInfo[]> = (text$: Observable<string>) =>
		text$.pipe(
			debounceTime(200),
			switchMap((term) => {
          if (!term || term.length <= 3) {
            return of([])
          }
          this.isLoading.set(true);
          return this.adminWikiArticlesClient.search(term).pipe(
            tap(() => this.isLoading.set(false))
          );
        }
			),
		);

	protected articleTypeaheadResultFormatter = (article: WikipediaArticleInfo) => article.title;

  // Icons
  protected searchIcon = IconConstants.ICON_SEARCH;
  protected waitingIcon = IconConstants.ICON_WAITING;

  // Methods
  public writeValue(wikiqid: any): void {
    this._selectedArticle.qid = wikiqid;
    this.qid.set(wikiqid);
  }
  
  public registerOnChange(fn: Function): void {
    this.formControlOnChange = fn;
  }

  public registerOnTouched(fn: Function): void {
    this.formControlOnTouched = fn;
  }

  public setDisabledState?(isDisabled: boolean): void {
    // this.disabled.set(isDisabled);
  }

  protected onSelect(article: WikipediaArticleInfo) {
    this.formControlOnTouched(article.qid);
    this.formControlOnChange(article.qid);
    this.qid.set(article.qid);
  }

}
