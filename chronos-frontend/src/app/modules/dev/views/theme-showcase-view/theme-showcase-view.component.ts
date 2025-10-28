import { CommonModule } from '@angular/common';
import {Component} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxColorsModule } from 'ngx-colors';

@Component({
  selector: 'chronos-theme-showcase-view',
  templateUrl: './theme-showcase-view.component.html',
  styleUrls: ['./theme-showcase-view.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    FontAwesomeModule,
    NgbDropdownModule,
    NgxColorsModule,
  ]
})
export class ThemeShowcaseViewComponent {

}
