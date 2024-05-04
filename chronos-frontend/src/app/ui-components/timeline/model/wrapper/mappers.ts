import {TimelineEntry} from "./timeline-entry.model";
import {Entry} from "../../../../model/entry.model";
import {TimelineTag} from "./timeline-tag";
import {Tag} from "../../../../model/tag.model";
import {DateRange} from "../../../../model/date-range.model";
import {TimelineDateRange} from "./timeline-date-range.model";

export function mapToTimelineEntry(entry: Entry): TimelineEntry {
  return {
    title: entry.title,
    subTitle: entry.subTitle,
    dateRanges: entry.dateRanges ? entry.dateRanges.map(d => mapToTimelineDateRange(d)) : [],
    tags: entry.tags?.map(mapToTimelineTag),
    original: entry
  }
}

export function mapToTimelineTag(tag: Tag): TimelineTag {
  return {
    name: tag.name,
    color: tag.color,
    category: tag.tagCategory?.name,
    icon: tag.tagCategory?.icon,
    original: tag
  }
}
export function mapToTimelineDateRange(dateRange: DateRange): TimelineDateRange {
  return {
    start: dateRange.start ? new Date(dateRange.start) : undefined,
    end: dateRange.end ? new Date(dateRange.end) : undefined,
    // TODO: Revise
    type: `${dateRange.type}`
  }
}
