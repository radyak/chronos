import {DateRange, DateRangeType} from "../model/date-range.model";

export function getEarliestStartDateRange(dateRanges: Array<DateRange> = []): DateRange {
  const toSort = [...dateRanges]
  toSort.sort((d1, d2) => d1.start! > d2.start! ? 1 : -1);
  return toSort[0];
}

/**
 * Creates a {@link DateRange} that spans from the earliest start to the latest end date.
 */
export function calculateMaxSpanningDateRange(dateRanges: Array<DateRange> = []): DateRange {
  return {
    start: getEarliestDateString(dateRanges.map(d => d.start!)),
    end: getLatestDateString(dateRanges.map(d => d.end!))
  }
}

export function getEarliestDateString(dates: Array<string>): string {
  if (!dates || !dates.length) {
    return '';
  }
  return dates.reduce(
    (a, b) => { return new Date(a) < new Date(b) ? a : b; }
  );
}

export function getLatestDateString(dates: Array<string>): string {
  if (!dates || !dates.length) {
    return '';
  }
  return dates.reduce(
    (a, b) => { return new Date(a) > new Date(b) ? a : b; }
  );
}

export function getDateRangeOfType(dateRanges: Array<DateRange> = [], type: DateRangeType): DateRange | null {
  return dateRanges.filter(d => d.type === type)[0] || null;
}
