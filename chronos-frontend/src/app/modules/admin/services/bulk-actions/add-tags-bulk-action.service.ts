import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Tag} from "../../../../model/tag.model";
import {Entry} from "../../../../model/entry.model";

interface AddTagsBulkAction {
  tagIds: Array<number>,
  entryIds: Array<number>
}


@Injectable({
  providedIn: null
})
export class AddTagsBulkActionService {

  constructor(private http: HttpClient) {
  }

  public addTagsToEntries(tags: Array<Tag>, entries: Array<Entry>): Observable<void> {
    const body: AddTagsBulkAction = {
      tagIds: tags.filter(t => !!t.id).map(t => t.id!),
      entryIds: entries.filter(e => !!e.id).map(e => e.id!)
    };
    return this.http.put<void>('/api/admin/bulk-actions/entries/add-tags', body)
  }

}
