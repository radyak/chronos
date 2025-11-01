import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MonoTypeOperatorFunction, Observable, tap} from "rxjs";
import {NotificationService} from "../../../common/components/notifications/notification.service";
import { Entity as Person } from 'src/app/common/model/domain/entity.model';

@Injectable({
  providedIn: 'root'
})
export class AdminPersonService {

  constructor(private http: HttpClient,
              private notificationService: NotificationService) { }

  public save(person: Person): Observable<Person> {
    if (person.id === undefined) {
      return this.create(person);
    } else {
      return this.update(person);
    }
  }

  public delete(person: Person): Observable<void> {
    return this.http.delete<void>(`/api/admin/persons/${person.id}`).pipe(
      this.successNotification(`Deleted person"`)
    );
  }

  private create(person: Person): Observable<Person> {
    return this.http.post<Person>("/api/admin/persons", person).pipe(
      this.successNotification(`Created person"`)
    );
  }

  private update(person: Person): Observable<Person> {
    return this.http.put<Person>(`/api/admin/persons/${person.id}`, person).pipe(
      this.successNotification(`Updated person"`)
    );
  }

  private successNotification(text: string): MonoTypeOperatorFunction<any> {
    return tap({
      next: () => this.notificationService.success(text)
    })
  }

}
