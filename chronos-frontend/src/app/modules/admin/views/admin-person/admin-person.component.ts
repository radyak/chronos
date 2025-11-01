import {Component, OnInit} from '@angular/core';
import {debounceTime, filter, map, Subject} from "rxjs";
import {faCircleNotch, faMagnifyingGlass, faPenToSquare, faPlus, faSpinner, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";
import { Entity } from 'src/app/common/model/domain/entity.model';
import { AdminPersonService } from '../../person/admin-person.service';
import { PersonService } from '../../person/entries.service';
import { WikiArticlesService } from '../../person/wiki-article.service';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor, NgForOf } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AdminWikiArticlesService } from '../../person/admin-wiki-article.service';
import { WikipediaArticleInfoComponent } from 'src/app/common/components/wikipedia-article-info/wikipedia-article-info.component';
import { WikipediaSummary } from 'src/app/common/model/general/wikipedia/wikipedia-summary.model';
import { WikipediaArticleInfo } from 'src/app/common/model/general/wikipedia/wikipedia-article-info.model';
import { DateInputComponent } from 'src/app/common/components/date-input/date-input.component';


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
export class AdminPersonComponent implements OnInit {

  wikipediaSummary?: WikipediaSummary;
  matchingArticles?: Array<WikipediaArticleInfo> = [];
  currentEntity?: Entity;

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

  protected setCurrentEntry(): void {
    const entityId = this.route.snapshot.params['id'];
    this.personService.getByIdentifier(entityId).subscribe(entity => {
      this.currentEntity = entity;
      this.loadWikipediaSummary(entity.qid);
      this.afterLoadEntity();
    })
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

