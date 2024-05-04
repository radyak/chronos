import {Component, Input} from '@angular/core';
import {RouterModule} from "@angular/router";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {CommonModule} from "@angular/common";
import {filter, map, Observable} from "rxjs";
import {
  EntriesSearchInputComponent
} from "../../functional-components/entries-search-input/entries-search-input.component";
import {AuthService} from "../../security/auth.service";


export interface MenuItem {
  label: string;
  routerLink?: string | string[];
  show$?: Observable<boolean>
}

@Component({
  standalone: true,
  selector: 'chronos-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  imports: [
    CommonModule,
    RouterModule,
    NgbModule,
    EntriesSearchInputComponent
  ]
})
export class NavbarComponent {
  isMenuCollapsed = true;

  @Input()
  title?: MenuItem;

  @Input()
  menuItems: Array<MenuItem> = [];

  constructor(private authService: AuthService) {
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  logout(): void {
    this.authService.logout();
  }

  userName$(): Observable<string | undefined> {
    return this.authService.profile$.pipe(
        filter(profile => !!profile),
        map(profile => {
          return profile.name
        })
    );
  }
}
