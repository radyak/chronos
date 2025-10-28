import {Injectable} from "@angular/core";
import { Entity } from "../model/domain/entity.model";


@Injectable({
  providedIn: 'root'
})
export class EntitiesGroupMergeService {

  public mergeGroups(entityGroups: Array<Array<Entity>>, doMerge: boolean = true): Array<Array<Entity>> {
    // This logical branch is intended to support generic usage with a toggle
    if (!doMerge) {
      return entityGroups;
    }
    return [ entityGroups.reduce((p, c) => p.concat(c)) ];
  }

}
