import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {TagCategory} from "../../../model/tag-category.model";

@Injectable({
  providedIn: null
})
export class TagCategoriesService {

  constructor(private http: HttpClient) { }

  public allTagCategories(): Observable<Array<TagCategory>> {
    return this.http.get<Array<TagCategory>>("/api/tag-categories")
  }

}
