import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Tag} from "../../../model/tag.model";

@Injectable({
  providedIn: null
})
export class TagsService {

  constructor(private http: HttpClient) { }

  public allTags(): Observable<Array<Tag>> {
    return this.http.get<Array<Tag>>("/api/tags")
  }

}
