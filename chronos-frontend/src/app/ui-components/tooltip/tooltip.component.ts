import {Component, Input} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgbModule, NgbTooltip} from "@ng-bootstrap/ng-bootstrap";
import {faQuestionCircle} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";


@Component({
  standalone: true,
  selector: 'chronos-tooltip',
  templateUrl: './tooltip.component.html',
  styleUrls: ['./tooltip.component.scss'],
  imports: [
    CommonModule,
    NgbModule,
    FontAwesomeModule
  ]
})
export class TooltipComponent {

  helpIcon = faQuestionCircle;
  active = false;

  @Input()
  inline: boolean = false;

  protected toggle(tooltip: NgbTooltip): void {
    this.active = !this.active;
    if (this.inline) {
      return;
    }
    if (this.active && tooltip.isOpen()) {
      tooltip.close();
    } else {
      tooltip.open();
    }
  }
}
