import {Directive, OnInit} from "@angular/core";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Observable} from "rxjs";

@Directive()
export abstract class AbstractQueryDrivenComponent implements OnInit {
  protected constructor(
    protected router: Router,
    protected route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const done = this.toClassFields(params);
      if (done instanceof Observable) {
        done.subscribe(() => this.search());
      } else {
        this.search();
      }
    })
  }

  updateSearchParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.toParams(),
      queryParamsHandling: 'merge'
    })
  }

  protected abstract search(): void;

  protected abstract toClassFields(params: Params): void | Observable<void>;

  protected abstract toParams(): Params;
}
