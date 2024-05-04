import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  NgZone,
  OnDestroy,
  Output,
  ViewChild
} from '@angular/core';
import {TimelineEntry} from "./model/wrapper/timeline-entry.model";
import {mapToTimelineEntry} from "./model/wrapper/mappers";
import {Entry} from "../../model/entry.model";
import {DatePipe, NgClass, NgForOf, NgIf, NgStyle} from "@angular/common";
import {TagCategory} from "../../model/tag-category.model";
import {ElementBox} from "./model/representation/element-box";
import {TimelineModel} from "./util/timeline-model";
import {TimelineConfig} from "./model/config/timeline-config";
import {TimelineElementRange} from "./model/representation/timeline-element";


@Component({
  standalone: true,
  selector: 'chronos-timeline',
  templateUrl: './timeline.component.html',
  imports: [
    NgForOf,
    NgIf,
    NgStyle,
    DatePipe,
    NgClass
  ],
  styleUrls: ['./timeline.component.scss']
})
export class TimelineComponent implements AfterViewInit, OnDestroy {

  timelineEntries: Array<Array<TimelineEntry>> = [];
  selectedEntry?: TimelineEntry;

  timelineModel?: TimelineModel;
  elementBox?: ElementBox;
  cursorLine?: number;
  cursorDate?: Date;

  resizeObserver: ResizeObserver;

  @ViewChild("stage")
  stageElement!: ElementRef<SVGElement>;

  @Output()
  entrySelected = new EventEmitter<Entry | undefined>();

  @Input()
  fromYear?: number;

  @Input()
  toYear?: number;

  @Input()
  config: TimelineConfig = {
    bars: {
      height: 10,
      distancePx: 3,
      defaultColor: 'gray',
      label: {
        fontSize: 10,
        color: 'white',
        opacity: 0.5,
        margin: 5
      }
    },
    groups: {
      flatten: true,
      distancePx: 3,
      line: {
        strokeColor: 'white',
        strokeOpacity: 0.25,
        strokeWidth: 0.5,
        strokeDashArray: "4 4",
        label: {
          fontSize: 10,
          color: 'white',
          opacity: 0.25,
          margin: 5
        }
      }
    },
    helperLines: {
      strokeColor: 'white',
      strokeOpacity: 0.25,
      strokeWidth: 1,
      label: {
        fontSize: 10,
        color: 'white',
        opacity: 0.25,
        margin: 5
      },
    },
    cursorLine: {
      strokeColor: 'white',
      strokeOpacity: 0.35,
      strokeWidth: 2,
      label: {
        fontSize: 16,
        color: 'white',
        opacity: 1,
        margin: 5
      },
    },
    borderPercent: 0.05,
    bottomOffsetPx: 10,
    lineYearIntervalConfigs: [
      {
        intervalRange: [0, 100],
        lineInterval: 10
      },
      {
        intervalRange: [100, 250],
        lineInterval: 25
      },
      {
        intervalRange: [250, 500],
        lineInterval: 100
      }
    ]
  };

  constructor(private zone: NgZone) {
    this.resizeObserver = new ResizeObserver(evt => {
      this.elementBox = {
        width: evt[0].contentRect.width,
        height: evt[0].contentRect.height,
      }
      this.zone.run(() => this.render());
    });
  }

  ngAfterViewInit() {
    this.resizeObserver.observe(this.stageElement.nativeElement)
  }

  ngOnDestroy(): void {
    this.resizeObserver.unobserve(this.stageElement.nativeElement);
  }

  @Input()
  set entries(entryGroups: Array<Array<Entry>>) {
    if (this.config.groups.flatten) {
      entryGroups = [ entryGroups.reduce((p, c) => p.concat(c)) ]
    }
    this.timelineEntries = entryGroups
      .map(entryGroup => entryGroup
          .map(mapToTimelineEntry)
          //.filter(entry => !!entry.start && !!entry.end)
      )
    this.render();
  }

  @Input()
  colorCategory?: TagCategory;

  private render(): void {
    if (!this.timelineEntries || this.timelineEntries.length === 0) {
      console.debug('No entries to render yet')
      return;
    }
    if (!this.elementBox) {
      console.debug('No stage to render yet')
      return;
    }
    const yearRange = (this.fromYear && this.toYear) ? {
        start: this.fromYear,
        end: this.toYear
      } : undefined;
    this.timelineModel = new TimelineModel(
      this.elementBox!,
      this.config,
      this.timelineEntries,
      yearRange
    );
  }

  getFill(entry: TimelineEntry): string {
    const colorTag = entry.original.tags?.find(tag => tag.tagCategory?.id === this.colorCategory?.id);
    return colorTag?.color || this.config.bars.defaultColor;
  }

  mousemove(event: any) {
    this.cursorLine = event.offsetX;
    this.cursorDate = this.timelineModel!.fromX(event.offsetX);
  }

  mouseout() {
    delete this.cursorLine;
    delete this.cursorDate;
  }

  cursorCloseToRightEnd(x: number, distancePx: number = 100) {
    return ((this.elementBox?.width || 0) - (x || 0)) > distancePx;
  }

  selectEntry(entry: TimelineEntry): void {
    if (this.selectedEntry === entry) {
      delete this.selectedEntry;
    } else {
      this.selectedEntry = entry;
    }
    this.entrySelected.emit(this.selectedEntry?.original);
  }

  calculateHeight(): number | undefined {
    const allYCoords = this.timelineModel?.timelineElementGroups
          .map(g => g.elements)
          .reduce((p, c) => p.concat(c))
          .map((t) => t.ranges)
          .reduce((max, current) => max.concat(current))
          .map(r => r.y)
          || []
    return (Math.max(...allYCoords) || 0) + this.config.bars.height
      + this.config.bottomOffsetPx
      + this.config.cursorLine.label.margin
      + this.config.helperLines.label.fontSize;
  }

  getMaxXOfRanges(ranges: Array<TimelineElementRange>): number {
    return Math.max(...ranges.map(r => (r.x + r.width)));
  }
}
