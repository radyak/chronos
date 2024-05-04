import {Tag} from "./tag.model";
import {DateRange} from "./date-range.model";

export interface Entry {
  id?: number;
  title?: string;
  subTitle?: string;
  dateRanges?: Array<DateRange>;
  tags?: Array<Tag>;
  wikipediaPage?: string;
}
