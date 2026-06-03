import {Component, input, Input} from '@angular/core';
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

  // Icons
  helpIcon = faQuestionCircle;

  // Properties
  active = false;
  
  // Inputs
  text = input<(string | undefined | null)[]>();
  inline = input(false);

  // Methods
  protected toggle(tooltip: NgbTooltip): void {
    this.active = !this.active;
    if (this.inline()) {
      return;
    }
    if (this.active && tooltip.isOpen()) {
      tooltip.close();
    } else {
      tooltip.open();
    }
  }

  protected hasContent(): boolean {
    if (!this.text()) {
      return true;
    }
    return this.text()!.filter(line => !!line).length > 0;
  }

}
