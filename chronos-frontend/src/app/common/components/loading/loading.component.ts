import { Component, HostBinding, input } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { IconConstants } from '../../constants/icon.constants';

@Component({
  selector: '[loading]',
  imports: [
    FontAwesomeModule
  ],
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss',
})
export class LoadingComponent {

  // Inputs
  loading = input.required<boolean>();

  // Icons
  loadingIcon = IconConstants.ICON_WAITING;

  @HostBinding('style.backdrop-filter')
  get backdropFilter() {
    return this.loading() ? 'blur(2px)' : 'none';
  }
}
