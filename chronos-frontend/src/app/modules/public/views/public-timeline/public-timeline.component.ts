import {Component, OnInit} from '@angular/core';
import {TagsService} from "../../service/tags.service";
import {combineLatest, map, Observable, shareReplay} from "rxjs";
import {Tag} from "../../../../model/tag.model";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {EntriesService} from "../../service/entries.service";
import {Entry} from "../../../../model/entry.model";
import {TagCategory} from "../../../../model/tag-category.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {TagCategoriesService} from "../../service/tag-categories.service";
import {
  faChartGantt,
  faMagnifyingGlassMinus,
  faMagnifyingGlassPlus,
  faPlus, faSort, faSortDown, faSortUp,
  faTable,
  faTrash
} from "@fortawesome/free-solid-svg-icons"
import {getEarliestStartDateRange} from "../../../../util/date-range.utils";
import {QueryDrivenComponent} from "../../../../common/query-driven-component.directive";
import {IconDefinition} from "@fortawesome/fontawesome-svg-core";

interface DisplayOption {
  id: string,
  label: string,
  icon: IconDefinition
}

interface EntrySortableProperty {
  key: 'title' | 'subTitle' | 'start' | 'end';
  label: string;
  compare: (e1: Entry, e2: Entry) => number;
}

type SortDirection = 'asc' | 'desc';

interface SortConfig {
  property: EntrySortableProperty,
  direction: SortDirection
}

@Component({
  selector: 'chronos-public-timeline',
  templateUrl: './public-timeline.component.html',
  styleUrls: ['./public-timeline.component.scss']
})
export class PublicTimelineComponent extends QueryDrivenComponent implements OnInit {

  showSearchInputs = false;

  showSearchIcon = faMagnifyingGlassPlus;
  hideSearchIcon = faMagnifyingGlassMinus;
  addIcon = faPlus;
  deleteIcon = faTrash;

  entries: Array<Array<Entry>> = [];
  tags$: Observable<Array<Tag>>;
  tagCategories: Array<TagCategory> = [];

  selectedEntrySummary?: WikipediaSummary | null;

  selectedTagGroups: Array<Array<Tag>> = [[]];
  title: string = '';
  from: number = 0;
  to: number = 0;
  colorCategoryId?: number;

  colorCategorySearch: string = '';

  display: string = 'table';

  displayOptions: Array<DisplayOption> = [
    {
      id: 'table',
      icon: faTable,
      label: 'Table'
    },
    {
      id: 'timeline',
      icon: faChartGantt,
      label: 'Timeline'
    },
  ];

  sortOptions: Array<EntrySortableProperty> = [
    {
      key: 'title',
      label: 'Title',
      compare: (e1: Entry, e2: Entry) => {
        if ((e1.title || '') < (e2.title || ''))
          return 1;
        if ((e1.title || '') > (e2.title || ''))
          return -1;
        return 0;
      }
    },
    {
      key: 'subTitle',
      label: 'Subtitle',
      compare: (e1: Entry, e2: Entry) => {
        if ((e1.subTitle || '') < (e2.subTitle || ''))
          return 1;
        if ((e1.subTitle || '') > (e2.subTitle || ''))
          return -1;
        return 0;
      }
    },
    {
      key: 'start',
      label: 'Earliest start date',
      compare: (e1: Entry, e2: Entry) => {
        return (getEarliestStartDateRange(e1.dateRanges).start! > getEarliestStartDateRange(e2.dateRanges).start! ? 1 : -1)
      }
    },
    {
      key: 'end',
      label: 'Latest end date',
      compare: (e1: Entry, e2: Entry) => {
        return (getEarliestStartDateRange(e1.dateRanges).end! > getEarliestStartDateRange(e2.dateRanges).end! ? 1 : -1)
      }
    }
  ];

  sortConfig?: SortConfig;

  get selectedColorTagCategory(): TagCategory | undefined {
    return this.tagCategories.find(c => c.id === this.colorCategoryId);
  }

  constructor(private tagService: TagsService,
              private tagCategoriesService: TagCategoriesService,
              private entriesService: EntriesService,
              protected override router: Router,
              protected override route: ActivatedRoute) {
    super(router, route);

    this.tags$ = this.tagService.allTags().pipe(shareReplay());
    this.tagCategoriesService.allTagCategories()
      .subscribe((tagCategories: Array<TagCategory>) => this.tagCategories = tagCategories);
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  selectColorTagCategory(tagCategory?: TagCategory): void {
    this.colorCategoryId = tagCategory?.id;
    this.updateSearchParams();
  }

  trackSelectedTagGroup(i: number) {
    return this.selectedTagGroups?.[i];
  }

  setShowSearchInputs(showSearchInputs: boolean): void {
    this.showSearchInputs = showSearchInputs;
    this.updateSearchParams();
  }

  onEntrySelected(entry?: Entry) {
    if (!entry) {
      delete this.selectedEntrySummary;
    } else {
      this.entriesService.getWikipediaSummary(entry.id!).subscribe({
        next: summary => {
          this.selectedEntrySummary = summary;
        },
        error: () => {
          this.selectedEntrySummary = null;
        }
      })
    }
  }

  filterTagCategory(tagCategory: TagCategory): boolean {
    return tagCategory.name.toLowerCase().includes(this.colorCategorySearch.toLowerCase());
  }

  addTagGroup(): void {
    this.selectedTagGroups.push([])
  }

  removeSelectedTagGroup(selectedTagGroup: Array<Tag>): void {
    this.selectedTagGroups.splice(this.selectedTagGroups.indexOf(selectedTagGroup), 1);
    if (this.selectedTagGroups.length === 0) {
      this.selectedTagGroups = [[]];
    }
    this.updateSearchParams();
  }

  protected keyDown(event: any) {
    if (event.key === 'Enter') {
      this.updateSearchParams();
    }
  }

  protected search(): void {
    if (!this.selectedTagGroups || this.selectedTagGroups.length === 0) {
      this.entriesService.find({
        title: this.title,
        from: this.from,
        to: this.to
      }).subscribe(entries =>
        this.entries = [
          entries.sort((e1, e2) => {
              if (!this.sortConfig) {
                return 0;
              }
              return this.sortConfig.property.compare(e1, e2) * (this.sortConfig.direction === 'asc' ? 1 : -1)
            }
          )
        ]
      );
    } else {
      combineLatest(
        this.selectedTagGroups.map(selectedTagGroup =>
          this.entriesService.find({
            tagIds: selectedTagGroup
              .filter(tag => !!tag?.id)
              .map(tag => tag.id!),
            title: this.title,
            from: this.from,
            to: this.to
          })
        )
      ).subscribe(entries => {
          entries.forEach(entry => entry.sort((e1, e2) => {
              if (!this.sortConfig) {
                return 0;
              }
              return this.sortConfig.property.compare(e1, e2) * (this.sortConfig.direction === 'asc' ? 1 : -1)
          }));
          this.entries = entries;
        }
      );
    }
  }

  protected toClassFields(params: Params): Observable<void> {
    return this.tags$.pipe(
      map(tags => {
        this.title = params['title'] || '';
        this.from = parseInt(params['from']) || 0;
        this.to = parseInt(params['to']) || 0;
        this.selectedTagGroups = (params['tags'] || '')
          .split(';')
          .map((selectedTagIdGroupString: string) => selectedTagIdGroupString
            .split(',')
            .map((id: string) => tags.find(tag => tag.id === parseInt(id)))
            .filter((tag) => !!tag)
          )
        this.colorCategoryId = parseInt(params['color-category']) || undefined;
        this.showSearchInputs = params['show-search-inputs'];
        this.display = params['display'] || 'table';

        const sortBy: EntrySortableProperty | undefined = this.sortOptions.find(o => o.key === params['sortBy']);
        if (sortBy) {
          this.sortConfig = {
            property: sortBy,
            direction: params['sortOrder']
          }
        }
        return;
      })
    )
  }

  protected toParams(): Params {
    return {
      'tags': this.selectedTagGroups.length > 0 ?
        this.selectedTagGroups
          .filter(selectedTagGroup => selectedTagGroup && selectedTagGroup.length > 0)
          .map(selectedTagGroup => selectedTagGroup
            .map(tag => tag.id)
            .filter(id => !!id)
            .join(',')
          ).join(';')
        : null,
      'title': this.title || null,
      'from': this.from || null,
      'to': this.to || null,
      'color-category': this.colorCategoryId || null,
      'show-search-inputs': this.showSearchInputs || null,
      'display': this.display || null,
      'sortBy': this.sortConfig?.property.key || null,
      'sortOrder': this.sortConfig?.direction || null,
    }
  }

  selectDisplayOption(id: string) {
    this.display = id;
    this.updateSearchParams();
  }

  sort(property: EntrySortableProperty | null, direction?: SortDirection): void {
    if (!property) {
      delete this.sortConfig;
    } else {
      this.sortConfig = {
        property: property,
        direction: direction || this.sortConfig?.direction || 'asc'
      }
    }
    this.updateSearchParams();
  }

  flatEntries(): Array<Entry> {
    return this.entries.reduce((p, c) => p.concat(c));
  }

  sortIcon(): IconDefinition {
    switch (this.sortConfig?.direction) {
      case 'asc': return faSortUp;
      case 'desc': return faSortDown;
      default: return faSort;
    }
  }

  rotateSort(): void {
    if (!this.sortConfig) {
      return;
    }
    this.sortConfig.direction = this.sortConfig.direction === 'asc' ? 'desc' : 'asc';
    this.updateSearchParams();
  }

}
