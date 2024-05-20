import {ElementBox} from "../model/representation/element-box";
import {TimelineEntry} from "../model/wrapper/timeline-entry.model";
import {TimeUtil} from "../../../util/time-util";
import {Line} from "../model/representation/line.model";
import {TimelineConfig, YearIntervalConfig} from "../model/config/timeline-config";
import {YearRange} from "../model/representation/year-range";
import {TimelineDateRange} from "../model/wrapper/timeline-date-range.model";
import {TimelineElementGroup} from "./timeline-element-group";

export class TimelineModel {

  // vars
  private readonly millisPerPx: number;
  private readonly offsetMillis: number;

  // VIEW ENTITIES
  public readonly helperLines: Array<Line>;
  public readonly timelineElementGroups: Array<TimelineElementGroup>;

  constructor(elementBox: ElementBox, config: TimelineConfig, timelineEntryGroups: Array<Array<TimelineEntry>> = [], yearRange?: YearRange) {
    const entriesFlat: Array<TimelineEntry> = timelineEntryGroups.reduce((p, c) => p.concat(c));
    const allDateRanges: Array<TimelineDateRange> = entriesFlat.map(e => e.dateRanges)
      .reduce((prev, current) => {
      return prev.concat(current)
    })
    const firstDate = yearRange ? TimeUtil.getDateForYear(yearRange.start) : TimeUtil.getEarliestDate(allDateRanges.map(range => range.start!))
    const lastDate = yearRange ? TimeUtil.getDateForYear(yearRange.end) : TimeUtil.getLatestDate(allDateRanges.map(range => range.end!))
    const millisRange = lastDate.getTime() - firstDate.getTime();
    const borderPercent = yearRange ? 0 : config.borderPercent;
    const totalMillisOnStage = millisRange * (1 + 2 * borderPercent);

    this.offsetMillis = firstDate.getTime() - millisRange * borderPercent;

    this.millisPerPx = totalMillisOnStage / elementBox.width;

    let lineIntervalYears = this.getHelperLineYearInterval(lastDate.getFullYear() - firstDate.getFullYear(), config.lineYearIntervalConfigs);
    const startYear = Math.ceil(firstDate.getFullYear()!/lineIntervalYears) * lineIntervalYears;
    this.helperLines = [];
    for (let year = startYear; year <= lastDate.getFullYear(); year += lineIntervalYears) {
      const date = TimeUtil.getDateForYear(year);
      this.helperLines.push({
        x: this.toX(date),
        date: date
      });
    }

    let totalElementCount = 0;
    this.timelineElementGroups =
    timelineEntryGroups.map((group, groupIndex) => {
      return {
        elements: group.map((entry, elementIndex) => {
          const element = {
            timelineEntry: entry,
            ranges: entry.dateRanges.map(r => ({
              x: this.toX(r.start!),
              y: totalElementCount * (config.bars.height + config.bars.distancePx) + groupIndex * config.groups.distancePx,
              width: (r.end?.getTime()! - r.start?.getTime()!) / this.millisPerPx
            }))
          }
          totalElementCount++;
          return element;
        })
      }
    })
  }

  toX(date: Date): number {
    return (date.getTime() - this.offsetMillis) / this.millisPerPx;
  }

  fromX(px: number): Date {
    return new Date((px * this.millisPerPx) + this.offsetMillis);
  }

  private getHelperLineYearInterval(yearRange: number, yearIntervalConfigs: Array<YearIntervalConfig>): number {
    for (let yearIntervalConfig of yearIntervalConfigs) {
      if (yearRange > yearIntervalConfig.intervalRange[0] && yearRange < yearIntervalConfig.intervalRange[1]) {
        return yearIntervalConfig.lineInterval;
      }
    }
    return 100;
  }

}
