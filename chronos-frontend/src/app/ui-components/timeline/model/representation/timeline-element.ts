import {TimelineEntry} from "../wrapper/timeline-entry.model";

export interface TimelineElement {
  timelineEntry: TimelineEntry;
  ranges: Array<TimelineElementRange>
}

export interface TimelineElementRange {
  x: number;
  y: number;
  width: number;
}
