import { Component, effect, forwardRef, inject, signal, WritableSignal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { AdminWikiService } from 'src/app/modules/admin/services/admin-wiki.service';
import { WikipediaArticleInfoComponent } from '../wikipedia-article-info/wikipedia-article-info.component';
import { WikipediaArticleInfo } from '../../model/wikipedia/wikipedia-article-info.model';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { IconConstants } from '../../constants/icon.constants';

@Component({
  selector: 'chronos-wiki-article-input',
  imports: [
    FormsModule,
    WikipediaArticleInfoComponent,
    FaIconComponent
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
  private _wikiqid: string = '';
  protected search = signal('');
  protected _search = '';
  protected disabled = signal(false);
  private formControlOnChange: Function = () => {};
  private formControlOnTouched: Function = () => {};
  protected articles: WritableSignal<WikipediaArticleInfo[]> = signal([]);

  // Injected Dependencies
  protected wikiArticlesResource = inject(AdminWikiService).articleSearch(this.search);

  // Icons
  protected searchIcon = IconConstants.ICON_SEARCH;
  protected waitingIcon = IconConstants.ICON_WAITING;

  // Init
  constructor() {
    effect(() => {
      const articles = this.wikiArticlesResource.value();
      if (articles && articles.length > 0) {
        this.articles.set(articles);
      }
    });
  }
  
  // Methods
  public writeValue(wikiqid: any): void {
    this._wikiqid = wikiqid;
  }
  
  public registerOnChange(fn: Function): void {
    this.formControlOnChange = fn;
  }

  public registerOnTouched(fn: Function): void {
    this.formControlOnTouched = fn;
  }

  public setDisabledState?(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  protected searchChange(searchTerm: any) {
    this.search.set(searchTerm);
  }

  protected onSelect(article: WikipediaArticleInfo) {
    this.articles.set([]);
    this._wikiqid = article.qid;
    this.formControlOnTouched(this._wikiqid);
    this.formControlOnChange(this._wikiqid);
  }

}
