import {Component} from '@angular/core';
import {faPenToSquare, faPlus, faSearch} from "@fortawesome/free-solid-svg-icons";
import {Observable, of} from "rxjs";
import {Tag} from "../../../../../model/tag.model";
import {AdminTagsService} from "../../../services/admin-tags.service";
import {AdminTagCategoriesService} from "../../../services/admin-tag-categories.service";
import {TagCategory} from "../../../../../model/tag-category.model";

@Component({
  selector: 'chronos-tags-section',
  templateUrl: './tags-section.component.html',
  styleUrls: ['./tags-section.component.scss']
})
export class TagsSectionComponent {

  tags$: Observable<Array<Tag>> = of([]);
  tagCategories$: Observable<Array<TagCategory>> = of([]);
  currentTag?: Tag;
  searchName: string = '';
  searchCategory: string = '';
  searchCategoryInSelection: string = '';

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;

  constructor(private adminTagsService: AdminTagsService,
              private adminTagCategoriesService: AdminTagCategoriesService) {
  }

  ngOnInit(): void {
    this.tags$ = this.adminTagsService.allTags();
    this.tagCategories$ = this.adminTagCategoriesService.allTagCategories();
  }

  editTag(tag: Tag): void {
    this.currentTag = {...tag};
  }

  newTag(): void {
    this.currentTag = {
      name: ""
    };
  }

  saveTag(): void {
    if (!this.currentTag) {
      return
    }
    this.adminTagsService.saveTag(this.currentTag).subscribe(() => {
      delete this.currentTag;
      this.tags$ = this.adminTagsService.allTags();
    })
  }

  cancelEditTag(): void {
    delete this.currentTag;
  }

  deleteTag(tag: Tag): void {
    this.adminTagsService.deleteTag(tag).subscribe(() => {
      delete this.currentTag;
      this.tags$ = this.adminTagsService.allTags();
    })
  }

  filterTag(tag: Tag): boolean {
    return (
      !!tag.name && tag.name.toLowerCase().includes(this.searchName.toLowerCase())
    ) && (
      !!tag.tagCategory && tag.tagCategory.name.toLowerCase().includes(this.searchCategory.toLowerCase())
    );
  }

  filterTagCategory(tagCategory: TagCategory): boolean {
    return tagCategory.name.toLowerCase().includes(this.searchCategoryInSelection.toLowerCase());
  }

  selectCategory(tagCategory: TagCategory): void {
    if (!this.currentTag) {
      return
    }
    this.currentTag.tagCategory = tagCategory;
  }
}
