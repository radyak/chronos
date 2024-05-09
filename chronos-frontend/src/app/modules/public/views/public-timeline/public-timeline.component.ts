import {Component, OnInit} from '@angular/core';
import {TagsService} from "../../service/tags.service";
import {combineLatest} from "rxjs";
import {Tag} from "../../../../model/tag.model";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {EntriesService} from "../../service/entries.service";
import {Entry} from "../../../../model/entry.model";
import {TagCategory} from "../../../../model/tag-category.model";
import {WikipediaSummary} from "../../../../model/wikipedia-summary.model";
import {TagCategoriesService} from "../../service/tag-categories.service";
import {faMagnifyingGlassMinus, faMagnifyingGlassPlus, faPlus, faTrash} from "@fortawesome/free-solid-svg-icons"
import {getEarliestStartDateRange} from "../../../../util/date-range.utils";

@Component({
  selector: 'chronos-public-timeline',
  templateUrl: './public-timeline.component.html',
  styleUrls: ['./public-timeline.component.scss']
})
export class PublicTimelineComponent implements OnInit {

  showSearchInputs = false;

  showSearchIcon = faMagnifyingGlassPlus;
  hideSearchIcon = faMagnifyingGlassMinus;
  addIcon = faPlus;
  deleteIcon = faTrash;

  entries: Array<Array<Entry>> = [];
  tags: Array<Tag> = [];
  tagCategories: Array<TagCategory> = [];

  selectedEntrySummary?: WikipediaSummary | null;

  selectedTagGroups: Array<Array<Tag>> = [[]];
  from: number = 0;
  to: number = 0;
  colorCategoryId?: number;

  colorCategorySearch: string = '';

  get selectedColorTagCategory(): TagCategory | undefined {
    return this.tagCategories.find(c => c.id === this.colorCategoryId);
  }

  constructor(private tagService: TagsService,
              private tagCategoriesService: TagCategoriesService,
              private entriesService: EntriesService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    const tags$ = this.tagService.allTags();
    combineLatest([
      tags$,
      this.activatedRoute.queryParams
    ]).subscribe(([tags, params]) => {
      this.from = parseInt(params['from']) || 0;
      this.to = parseInt(params['to']) || 0;
      this.selectedTagGroups = (params['tags'] || '')
        .split(';')
        .map((selectedTagIdGroupString: string) => selectedTagIdGroupString
          .split(',')
          .map((id: string) => tags.find(tag => tag.id === parseInt(id)))
          .filter((tag) => !!tag)
        )
      this.colorCategoryId = parseInt(params['color-category']) || undefined
      this.showSearchInputs = params['show-search-inputs']
      this.searchEntries();
    });

    tags$.subscribe((tags: Array<Tag>) => this.tags = tags);
    this.tagCategoriesService.allTagCategories()
      .subscribe((tagCategories: Array<TagCategory>) => this.tagCategories = tagCategories);
  }

  searchEntries(): void {
    this.entries = []

    if (!this.selectedTagGroups || this.selectedTagGroups.length === 0) {
      this.entriesService.find({
        from: this.from,
        to: this.to
      }).subscribe(entries =>
        this.entries = [
          entries.sort((e1, e2) => ((
            getEarliestStartDateRange(e1.dateRanges).start! > getEarliestStartDateRange(e2.dateRanges).start!) ? 1 : -1)
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
            from: this.from,
            to: this.to
          })
        )
      ).subscribe(entries => {
        entries.forEach(entry => entry.sort((e1, e2) =>
          getEarliestStartDateRange(e1.dateRanges).start! > getEarliestStartDateRange(e2.dateRanges).start! ? 1 : -1)
        );
        this.entries = entries;
      }
    );
    }
  }

  selectColorTagCategory(tagCategory?: TagCategory): void {
    this.colorCategoryId = tagCategory?.id;
    this.updateQueryParams();
  }

  trackSelectedTagGroup(i: number) {
    return this.selectedTagGroups?.[i];
  }

  updateQueryParams(): void {
    const params: Params = {
      'tags': this.selectedTagGroups.length > 0 ?
        this.selectedTagGroups
          .filter(selectedTagGroup => selectedTagGroup && selectedTagGroup.length > 0)
          .map(selectedTagGroup => selectedTagGroup
            .map(tag => tag.id)
            .filter(id => !!id)
            .join(',')
          ).join(';')
        : null,
      'from': this.from || null,
      'to': this.to || null,
      'color-category': this.colorCategoryId || null,
      'show-search-inputs': this.showSearchInputs || null
    }
    this.router.navigate(
      [],
      {
        relativeTo: this.activatedRoute,
        queryParams: params,
        queryParamsHandling: 'merge'
      }
    );
  }

  setShowSearchInputs(showSearchInputs: boolean): void {
    this.showSearchInputs = showSearchInputs;
    this.updateQueryParams();
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
    this.updateQueryParams();
  }

}
