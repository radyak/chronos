import {Directive, OnInit} from "@angular/core";
import {ActivatedRoute, Params, Router} from "@angular/router";

@Directive()
export abstract class QueryDrivenComponent implements OnInit {
  protected constructor(
    protected router: Router,
    protected route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.toClassFields(params);
      this.search();
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

  protected abstract toClassFields(params: Params): void;

  protected abstract toParams(): Params;
}
