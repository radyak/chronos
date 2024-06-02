import {Injectable} from "@angular/core";
import {Entry} from "../model/entry.model";
import {RelationType} from "../model/relation-type.model";
import {Relation} from "../model/relation.model";


@Injectable({
  providedIn: 'root'
})
export class RelationService {

  public readonly VALUE_PLACEHOLDER = '{value}'

  public getRelationDescription(label: string, value?: string): string {
    return label.replaceAll(this.VALUE_PLACEHOLDER, value || 'NO VALUE');
  }

}
