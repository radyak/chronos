import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { debounceTime, filter, map, Subject, Subscription, switchMap } from "rxjs";
import { DateInputComponent } from 'src/app/common/components/date-input/date-input.component';
import { WikipediaArticleInfoComponent } from 'src/app/common/components/wikipedia-article-info/wikipedia-article-info.component';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { Entity } from 'src/app/common/model/domain/entity.model';
import { WikipediaArticleInfo } from 'src/app/common/model/general/wikipedia/wikipedia-article-info.model';
import { WikipediaSummary } from 'src/app/common/model/general/wikipedia/wikipedia-summary.model';
import { AdminPersonService } from '../../person/admin-person.service';
import { AdminWikiArticlesService } from '../../person/admin-wiki-article.service';
import { PersonService } from '../../person/entries.service';
import { WikiArticlesService } from '../../person/wiki-article.service';


@Component({
  selector: 'chronos-admin-person',
  templateUrl: './admin-person.component.html',
  styleUrls: ['./admin-person.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    WikipediaSummaryComponent,
    RouterModule,
    FormsModule,
    FontAwesomeModule,
    DateInputComponent,
    WikipediaArticleInfoComponent,
  ]
})
export class AdminPersonComponent implements OnInit, OnDestroy {

  wikipediaSummary?: WikipediaSummary;
  matchingArticles?: Array<WikipediaArticleInfo> = [];
  currentEntity?: Entity;
  paramSubscription?: Subscription;

  wikipediaTitleSearch$ = new Subject<string | undefined>();
  wikipediaSearchTerm: string = '';
  searchWikipediaArticlesLoading: boolean = false;

  searchIcon = faMagnifyingGlass;
  loadingIcon = faSpinner;

  constructor(private adminPersonService: AdminPersonService,
              private personService: PersonService,
              private adminWikiArticlesService: AdminWikiArticlesService,
              private wikiArticlesService: WikiArticlesService,
              private router: Router,
              private route: ActivatedRoute) {
    this.wikipediaTitleSearch$.pipe(
      filter(q => (!!q && q.length >= 3)),
      map(q => q!.trim()),
      debounceTime(700)
    ).subscribe(q => this.searchWikipediaArticles(q))
  }

  ngOnInit(): void {
    this.setCurrentEntry();
  }

  ngOnDestroy(): void {
    this.paramSubscription?.unsubscribe();
  }

  protected setCurrentEntry(): void {
    this.paramSubscription = this.route.params.pipe(switchMap(params => {
      const entityId = params['id'];
      return this.personService.getByIdentifier(entityId);
    }))
    .subscribe(entity => {
      this.currentEntity = entity;
      this.loadWikipediaSummary(entity.qid);
      this.afterLoadEntity();
    });
  }

  protected pageTitle(): string {
    return this.currentEntity?.key || '';
  }

  protected breadCrumbTitle(): string {
    return this.currentEntity?.key || '';
  }

  protected afterLoadEntity(): void {
    // To be overwritten from extending classes
  }

  private searchWikipediaArticles(query?: string): void {
    if (!query) {
      return;
    }
    this.searchWikipediaArticlesLoading = true;
    this.adminWikiArticlesService.search(query!).subscribe(articles => {
      this.searchWikipediaArticlesLoading = false;
      this.matchingArticles = articles;
    })
  }

  private loadWikipediaSummary(qid?: string): void {
    if (!qid) {
      return;
    }
    this.wikiArticlesService.getArticleByQid(qid!).subscribe(wikipediaSummary => {
      this.wikipediaSummary = wikipediaSummary;
    })
  }

  saveEntity(): void {
    if (!this.currentEntity) {
      return
    }
    this.adminPersonService.save(this.currentEntity).subscribe(() => {
      this.goBack();
    });
  }

  assignArticleToCurrentEntity(article: WikipediaArticleInfo): void {
    if (!this.currentEntity) {
      return;
    }
    this.currentEntity.qid = article.qid;
    this.loadWikipediaSummary(this.currentEntity.qid);
  }

  cancelEditEntity(): void {
    this.goBack();
  }

  goBack(): void {
    this.router.navigate(['..'], { relativeTo: this.route });
  }

  deleteEntity(entity: Entity): void {
    this.adminPersonService.delete(entity).subscribe(() => {
      this.goBack();
    })
  }

  triggerSearch() {
    this.wikipediaTitleSearch$.next(this.wikipediaSearchTerm);
  }

}

