import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {Observable} from "rxjs";

export interface Type {
  id?: string;
  key?: string;
  from?: string;
  to?: string;
  qid?: string;
}


/**
 * @deprecated
 */
@Injectable({
  providedIn: 'root'
})
export class EntitiesClient {

  constructor(private http: HttpClient) { }

  public getRandomEntityWithQid$(): Observable<Type> {
    return this.http.get<Type>(`/api/entities/random`)
  }

}
