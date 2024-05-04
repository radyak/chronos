import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TagCategory} from "../../../model/tag-category.model";
import {MonoTypeOperatorFunction, Observable, tap} from "rxjs";
import {NotificationService} from "../../../ui-components/notifications/notification.service";

@Injectable({
  providedIn: null
})
export class AdminTagCategoriesService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public allTagCategories(): Observable<Array<TagCategory>> {
    return this.http.get<Array<TagCategory>>("/api/admin/tag-categories")
  }

  public saveTagCategory(tagCategory: TagCategory): Observable<TagCategory> {
    if (tagCategory.id === undefined) {
      return this.createTagCategory(tagCategory);
    } else {
      return this.updateTagCategory(tagCategory);
    }
  }

  public deleteTagCategory(tagCategory: TagCategory): Observable<void> {
    return this.http.delete<void>(`/api/admin/tag-categories/${tagCategory.id}`).pipe(
      this.successNotification(`Deleted category "${tagCategory.name}"`)
    );
  }

  private createTagCategory(tagCategory: TagCategory): Observable<TagCategory> {
    return this.http.post<TagCategory>("/api/admin/tag-categories", tagCategory).pipe(
      this.successNotification(`Created category "${tagCategory.name}"`)
    );
  }

  private updateTagCategory(tagCategory: TagCategory): Observable<TagCategory> {
    return this.http.put<TagCategory>("/api/admin/tag-categories", tagCategory).pipe(
      this.successNotification(`Updated category "${tagCategory.name}"`)
    );
  }

  private successNotification(text: string): MonoTypeOperatorFunction<any> {
    return tap({
      next: () => this.notificationService.success(text)
    })
  }

}
