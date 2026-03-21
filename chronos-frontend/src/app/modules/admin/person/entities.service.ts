import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {Observable} from "rxjs";
import { Entity } from 'src/app/common/model/domain/entity.model';

@Injectable({
  providedIn: 'root'
})
export class EntitiesService {

  constructor(private http: HttpClient) { }

  public getByIdentifier$(identifier: string): Observable<Entity> {
    return this.http.get<Entity>(`/api/entities/${identifier}`)
  }

  public getRandomEntityWithQid$(): Observable<Entity> {
    return this.http.get<Entity>(`/api/entities/random`)
  }

}
