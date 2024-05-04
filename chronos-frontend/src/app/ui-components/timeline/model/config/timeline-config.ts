
interface TextConfig {
  fontSize: number;
  color: string;
  opacity: number;
  margin: number;
}
interface LineConfig {
  strokeColor: string;
  strokeWidth: number;
  strokeOpacity: number;
  strokeDashArray?: string;
  label: TextConfig;
}

export interface YearIntervalConfig {
  intervalRange: Array<number>,
  lineInterval: number
}

export interface TimelineConfig {
  bars: {
    height: number;
    distancePx: number;
    defaultColor: string;
    label: TextConfig;
  };
  groups: {
    flatten: boolean;
    distancePx: number;
    line: LineConfig;
  },
  helperLines: LineConfig,
  cursorLine: LineConfig,
  borderPercent: number,
  bottomOffsetPx: number,
  lineYearIntervalConfigs: Array<YearIntervalConfig>;
}
