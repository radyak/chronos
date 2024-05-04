import {Tag} from "../../../../model/tag.model";
import {Entry} from "../../../../model/entry.model";
import {TimelineDateRange} from "./timeline-date-range.model";

export interface TimelineEntry {
  title?: string;
  subTitle?: string;
  dateRanges: Array<TimelineDateRange>
  tags?: Array<Tag>;
  original: Entry;
}
