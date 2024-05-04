import {Component} from '@angular/core';
import {faPenToSquare, faPlus, faSearch} from "@fortawesome/free-solid-svg-icons";
import {Observable, of} from "rxjs";
import {TagCategory} from "../../../../../model/tag-category.model";
import {AdminTagCategoriesService} from "../../../services/admin-tag-categories.service";

@Component({
  selector: 'chronos-tag-categories-section',
  templateUrl: './tag-categories-section.component.html',
  styleUrls: ['./tag-categories-section.component.scss']
})
export class TagCategoriesSectionComponent {

  tagCategories$: Observable<Array<TagCategory>> = of([]);
  currentTagCategory?: TagCategory;
  searchTagCategory: string = '';

  editIcon = faPenToSquare;
  newIcon = faPlus;
  searchIcon = faSearch;

  constructor(private adminTagCategoriesService: AdminTagCategoriesService) {
  }

  ngOnInit(): void {
    this.tagCategories$ = this.adminTagCategoriesService.allTagCategories();
  }

  editTagCategory(tagCategory: TagCategory): void {
    this.currentTagCategory = {...tagCategory};
  }

  newTagCategory(): void {
    this.currentTagCategory = {
      name: ""
    };
  }

  saveTagCategory(): void {
    if (!this.currentTagCategory) {
      return
    }
    this.adminTagCategoriesService.saveTagCategory(this.currentTagCategory).subscribe(() => {
      delete this.currentTagCategory;
      this.tagCategories$ = this.adminTagCategoriesService.allTagCategories();
    })
  }

  cancelEditTagCategory(): void {
    delete this.currentTagCategory;
  }

  deleteTagCategory(tagCategory: TagCategory): void {
    this.adminTagCategoriesService.deleteTagCategory(tagCategory).subscribe(() => {
      delete this.currentTagCategory;
      this.tagCategories$ = this.adminTagCategoriesService.allTagCategories();
    })
  }

  filterTagCategory(tagCategory: TagCategory): boolean {
    return tagCategory.name.toLowerCase().includes(this.searchTagCategory.toLowerCase())
  }

}
