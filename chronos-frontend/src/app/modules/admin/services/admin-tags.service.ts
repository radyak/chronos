import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MonoTypeOperatorFunction, Observable, tap} from "rxjs";
import {Tag} from "../../../model/tag.model";
import {NotificationService} from "../../../ui-components/notifications/notification.service";

@Injectable({
  providedIn: null
})
export class AdminTagsService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public allTags(): Observable<Array<Tag>> {
    return this.http.get<Array<Tag>>("/api/admin/tags")
  }

  public saveTag(tag: Tag): Observable<Tag> {
    if (tag.id === undefined) {
      return this.createTag(tag);
    } else {
      return this.updateTag(tag);
    }
  }

  public deleteTag(tag: Tag): Observable<void> {
    return this.http.delete<void>(`/api/admin/tags/${tag.id}`).pipe(
      this.successNotification(`Deleted tag "${tag.name}"`)
    );
  }

  private createTag(tag: Tag): Observable<Tag> {
    return this.http.post<Tag>("/api/admin/tags", tag).pipe(
      this.successNotification(`Created tag "${tag.name}"`)
    );
  }

  private updateTag(tag: Tag): Observable<Tag> {
    return this.http.put<Tag>("/api/admin/tags", tag).pipe(
      this.successNotification(`Updated tag "${tag.name}"`)
    );
  }

  private successNotification(text: string): MonoTypeOperatorFunction<any> {
    return tap({
      next: () => this.notificationService.success(text)
    })
  }

}
