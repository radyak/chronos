import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MonoTypeOperatorFunction, Observable, tap} from "rxjs";
import {Tag} from "../../../model/tag.model";
import {NotificationService} from "../../../ui-components/notifications/notification.service";

@Injectable({
  providedIn: null
})
export class TagsService {

  constructor(private http: HttpClient) { }

  public allTags(): Observable<Array<Tag>> {
    return this.http.get<Array<Tag>>("/api/tags")
  }

}
