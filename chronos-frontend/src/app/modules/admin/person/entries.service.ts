import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import { Entity as Person } from 'src/app/common/model/domain/entity.model';

export interface PersonSearchParams {
  from?: number;
  to?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  constructor(private http: HttpClient) { }

  public getByIdentifier(identifier: string): Observable<Person> {
    return this.http.get<Person>(`/api/persons/${identifier}`)
  }

  public getAll(): Observable<Array<Person>> {
    return this.http.get<Array<Person>>("/api/persons")
  }

  public find(PersonSearchParams: PersonSearchParams): Observable<Array<Person>> {
    let params = new HttpParams();
    if (PersonSearchParams.from) {
      params = params.set('from', PersonSearchParams.from)
    }
    if (PersonSearchParams.to) {
      params = params.set('to', PersonSearchParams.to)
    }
    return this.http.get<Array<Person>>("/api/persons", { params })
  }

}
