export interface DateRange {
  start?: string;
  end?: string;
  type?: DateRangeType;
}

export enum DateRangeType {
  LIFE = "LIFE",
  REIGN = "REIGN"
}
