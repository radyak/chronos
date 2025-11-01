import {Component} from '@angular/core';
import {version} from "src/environments/version";

@Component({
  standalone: true,
  selector: 'chronos-footer, [chronos-footer]',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {

  get version() {
    return `${version}`;
  }
}
