import {inject, Injectable} from "@angular/core";
import { filter, map, Observable, startWith } from "rxjs";
import { Breadcrumb } from "./breadcrumb.model";
import { ActivatedRoute, NavigationEnd, Router } from "@angular/router";

@Injectable({
  providedIn: "root"
})
export class BreadcrumbsService {
  private router: Router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);
  
  public readonly breadcrumbs$: Observable<Breadcrumb[]>;
  
  constructor() {
    this.breadcrumbs$ = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      startWith(null),
      map(() => this.buildBreadcrumbs(this.route.root)),
    );
  }

  private buildBreadcrumbs(
    route: ActivatedRoute,
    url = '',
    breadcrumbs: Breadcrumb[] = []
  ): Breadcrumb[] {
    const children: ActivatedRoute[] = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
      if (routeURL !== '') {
        url += `/${routeURL}`;
      }

      const label = child.snapshot.data['breadCrumb'];
      if (!!label && !breadcrumbs.find(breadcrumb => breadcrumb.url === url)) {
        breadcrumbs.push({label, url});
      }

      return this.buildBreadcrumbs(child, url, breadcrumbs);
    }

    return breadcrumbs;
  }
}
