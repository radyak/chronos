import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {Observable} from "rxjs";
import { Type } from 'src/app/common/model/domain/type.model';

@Injectable({
  providedIn: 'root'
})
export class EntitiesService {

  constructor(private http: HttpClient) { }

  public getByIdentifier$(identifier: string): Observable<Type> {
    return this.http.get<Type>(`/api/entities/${identifier}`)
  }

  public getRandomEntityWithQid$(): Observable<Type> {
    return this.http.get<Type>(`/api/entities/random`)
  }

}
