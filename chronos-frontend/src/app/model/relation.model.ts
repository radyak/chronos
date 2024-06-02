import {RelationType} from "./relation-type.model";
import {Entry} from "./entry.model";

export interface Relation {
  id?: number;
  type: RelationType;
  fromId: number;
  from: Entry;
  toId: number;
  to: Entry;
  value?: string;
}
