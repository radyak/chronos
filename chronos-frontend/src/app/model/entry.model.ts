import {Tag} from "./tag.model";
import {DateRange} from "./date-range.model";
import {Relation} from "./relation.model";

export interface Entry {
  id?: number;
  title?: string;
  subTitle?: string;
  wikipediaPage?: string;
  dateRanges?: Array<DateRange>;
  tags?: Array<Tag>;
  relations?: Array<Relation>;
}
