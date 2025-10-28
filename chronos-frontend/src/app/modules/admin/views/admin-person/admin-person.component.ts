import {Component, OnInit} from '@angular/core';
import {debounceTime, filter, map, Subject} from "rxjs";
import {faPenToSquare, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";
import { Entity } from 'src/app/model/domain/entity.model';
import { AdminPersonService } from '../../person/admin-person.service';
import { PersonService } from '../../person/entries.service';
import { WikiArticlesService } from '../../person/wiki-article.service';
import { WikipediaSummaryComponent } from 'src/app/ui-components/wikipedia-summary/wikipedia-summary.component';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgFor, NgForOf } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DateInputComponent } from 'src/app/ui-components/date-input/date-input.component';
import { AdminWikiArticlesService } from '../../person/admin-wiki-article.service';
import { WikipediaArticleInfo } from 'src/app/model/wikipedia-article-info.model';
import { WikipediaArticleInfoComponent } from 'src/app/ui-components/wikipedia-article-info/wikipedia-article-info.component';


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

  editIcon = faPenToSquare;
  newIcon = faPlus;
  deleteIcon = faTrash;

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
    this.personService.load(entityId).subscribe(entity => {
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
    this.adminWikiArticlesService.search(query!).subscribe(articles => {
      this.matchingArticles = articles;
    })
  }

  private loadWikipediaSummary(qid?: string): void {
    if (!qid) {
      return;
    }
    this.wikiArticlesService.load(qid!).subscribe(wikipediaSummary => {
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

