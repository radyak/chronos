import {Component} from '@angular/core';
import {AdminPersonComponent} from "../admin-person/admin-person.component";
import { CommonModule } from '@angular/common';
import { WikipediaSummaryComponent } from 'src/app/ui-components/wikipedia-summary/wikipedia-summary.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DateInputComponent } from 'src/app/ui-components/date-input/date-input.component';
import { WikipediaArticleInfoComponent } from 'src/app/ui-components/wikipedia-article-info/wikipedia-article-info.component';


@Component({
  selector: 'chronos-admin-new-person',
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
export class AdminNewPersonComponent extends AdminPersonComponent {

  override setCurrentEntry(): void {
    this.currentEntity = {
      key: "",
      from: "",
      to: ""
    };
  }

  override afterLoadEntity(): void {
    delete this.currentEntity?.id;
  }

  override pageTitle(): string {
    return `New Person`;
  }

  override breadCrumbTitle(): string {
    return `New Person`;
  }

}

