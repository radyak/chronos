import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MonoTypeOperatorFunction, Observable, tap} from "rxjs";
import {NotificationService} from "../../../ui-components/notifications/notification.service";
import {RelationType} from "../../../model/relation-type.model";

@Injectable({
  providedIn: null
})
export class AdminRelationTypesService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public allRelationTypes(): Observable<Array<RelationType>> {
    return this.http.get<Array<RelationType>>("/api/admin/relation-types")
  }

  public saveRelationType(relationType: RelationType): Observable<RelationType> {
    if (relationType.id === undefined) {
      return this.createRelationType(relationType);
    } else {
      return this.updateRelationType(relationType);
    }
  }

  public deleteRelationType(relationType: RelationType): Observable<void> {
    return this.http.delete<void>(`/api/admin/relation-types/${relationType.id}`).pipe(
      this.successNotification(`Deleted relation type "${relationType.name}"`)
    );
  }

  private createRelationType(relationType: RelationType): Observable<RelationType> {
    return this.http.post<RelationType>("/api/admin/relation-types", relationType).pipe(
      this.successNotification(`Created relation type "${relationType.name}"`)
    );
  }

  private updateRelationType(relationType: RelationType): Observable<RelationType> {
    return this.http.put<RelationType>("/api/admin/relation-types", relationType).pipe(
      this.successNotification(`Updated relation type "${relationType.name}"`)
    );
  }

  private successNotification(text: string): MonoTypeOperatorFunction<any> {
    return tap({
      next: () => this.notificationService.success(text)
    })
  }

}
