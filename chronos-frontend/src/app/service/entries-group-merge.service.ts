import {Injectable} from "@angular/core";
import {Entry} from "../model/entry.model";


@Injectable({
  providedIn: 'root'
})
export class EntriesGroupMergeService {

  public mergeGroups(entryGroups: Array<Array<Entry>>, doMerge: boolean = true): Array<Array<Entry>> {
    // This logical branch is intended to support generic usage with a toggle
    if (!doMerge) {
      return entryGroups;
    }
    return [ entryGroups.reduce((p, c) => p.concat(c)) ];
  }

}
