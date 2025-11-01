import {Component} from '@angular/core';
import {AdminPersonComponent} from "../admin-person/admin-person.component";
import { CommonModule } from '@angular/common';
import { WikipediaSummaryComponent } from 'src/app/common/components/wikipedia-summary/wikipedia-summary.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { WikipediaArticleInfoComponent } from 'src/app/common/components/wikipedia-article-info/wikipedia-article-info.component';
import { DateInputComponent } from 'src/app/common/components/date-input/date-input.component';


@Component({
  selector: 'chronos-admin-copy-person',
  templateUrl: '../admin-person/admin-person.component.html',
  styleUrls: ['../admin-person/admin-person.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    WikipediaSummaryComponent,
    RouterModule,
    FormsModule,
    FontAwesomeModule,
    DateInputComponent,
    WikipediaArticleInfoComponent
  ]
})
export class AdminCopyPersonComponent extends AdminPersonComponent {

  override afterLoadEntity(): void {
    delete this.currentEntity?.id;
  }

  override pageTitle(): string {
    return `Copy ${this.currentEntity?.key}`;
  }

  override breadCrumbTitle(): string {
    return `Copy ${this.currentEntity?.key}`;
  }

}

