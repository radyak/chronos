import {Component} from '@angular/core';
import {MenuItem} from "./ui-components/navbar/navbar.component";
import {of} from "rxjs";

@Component({
  selector: 'chronos-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  title: MenuItem = {
    label: 'Chronos'
  };

  menuItems: Array<MenuItem> = [
    {
      label: 'Home',
      routerLink: ''
    },
    {
      label: 'Discover',
      routerLink: 'discover'
    },
    {
      label: 'Admin',
      routerLink: 'admin',
      show$: of(true)
    }
  ];

}
