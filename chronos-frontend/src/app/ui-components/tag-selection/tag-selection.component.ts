import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule, NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TagComponent} from "../tag/tag.component";
import {Tag} from "../../model/tag.model";
import {NgbTypeahead} from "@ng-bootstrap/ng-bootstrap";
import {debounceTime, map, Observable, OperatorFunction} from "rxjs";

@Component({
  standalone: true,
  selector: 'chronos-tag-selection',
  templateUrl: './tag-selection.component.html',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    TagComponent,
    CommonModule,
    FormsModule,
    NgbTypeahead
  ],
  styleUrls: ['./tag-selection.component.scss']
})
export class TagSelectionComponent {
  searchTagString: string = '';

  selected?: Tag;

  @Input()
  public tags: Array<Tag> = [];

  @Output()
  public tagsChange: EventEmitter<Array<Tag>> = new EventEmitter<Array<Tag>>();

  @Input()
  public availableTags: Array<Tag> = [];

  filterAvailableTags(): Array<Tag> {
    const filter = this.searchTagString.toLowerCase();
    if (!filter) {
      return this.availableTags;
    }
    return this.availableTags.filter(tag =>
      (
        tag.name?.toLowerCase().includes(filter)
        || tag.tagCategory?.name?.toLowerCase().includes(filter)
      )
    );
  }

  searchTags: OperatorFunction<string, readonly Tag[]> = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(200),
      map((term) =>
        term === '' ? [] : this.availableTags.filter(
          (tag) => {
            return tag && `${tag.tagCategory?.name}: ${tag.name}`.toLowerCase().indexOf(term.toLowerCase()) > -1
              && !this.isSelected(tag)
          }
        ).slice(0, 10),
      ),
    );
  }

  tagFormatter = () => '';

  removeTag(tag: Tag): void {
    if (!tag || !this.isSelected(tag)) {
      return
    }
    this.tags.splice(this.tags.indexOf(tag), 1);
    this.tagsChange.emit(this.tags);
  }

  addTag(tag: Tag): void {
    if (!tag || this.isSelected(tag)) {
      return
    }
    this.tags.push(tag);
    this.tagsChange.emit(this.tags);
  }

  private isSelected(tag: Tag): boolean {
    return this.tags.map(t => t.id).indexOf(tag.id) !== -1;
  }

}
