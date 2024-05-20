import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {CommonModule, NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TagComponent} from "../tag/tag.component";
import {Tag} from "../../model/tag.model";
import {NgbTypeahead} from "@ng-bootstrap/ng-bootstrap";
import {distinctUntilChanged, filter, map, merge, Observable, OperatorFunction, Subject} from "rxjs";
import {Clipboard, ClipboardModule} from "@angular/cdk/clipboard";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faCopy} from "@fortawesome/free-solid-svg-icons";
import {NotificationService} from "../notifications/notification.service";
import {tagMatches} from "../../util/tag-match.function";

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
    NgbTypeahead,
    ClipboardModule,
    FontAwesomeModule
  ],
  styleUrls: ['./tag-selection.component.scss']
})
export class TagSelectionComponent {
  copyIcon = faCopy;

  @Input()
  public tags: Array<Tag> = [];

  @Output()
  public tagsChange: EventEmitter<Array<Tag>> = new EventEmitter<Array<Tag>>();

  @Input()
  public availableTags: Array<Tag> = [];

  @ViewChild('instance', { static: true }) instance!: NgbTypeahead;

  focus$ = new Subject<string>();
  click$ = new Subject<string>();

  constructor(private clipboard: Clipboard,
              private notificationService: NotificationService) {
  }

  searchTags: OperatorFunction<string, readonly Tag[]> = (text$: Observable<string>) => {
    const debouncedText$ = text$.pipe(distinctUntilChanged());
    const clicksWithClosedPopup$ = this.click$.pipe(filter(() => !this.instance.isPopupOpen()));
    const inputFocus$ = this.focus$;

    return merge(debouncedText$, clicksWithClosedPopup$, inputFocus$).pipe(
      distinctUntilChanged(),
      map((term) =>
        this.availableTags.filter(
          (tag) => {
            return tagMatches(term, tag) && !this.isSelected(tag)
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

  copyTags(): void {
    const tagIdList = this.tags.map(tag => tag.id).join(",");
    this.clipboard.copy(tagIdList);
    this.notificationService.success("Copied assigned tags");
  }

  onPaste(event: ClipboardEvent) {
    event.preventDefault();
    let clipboardData = event.clipboardData;
    let pastedText = clipboardData?.getData('text');
    if (pastedText) {
      const tags = pastedText.split(",")
        .map(pastedTagId => parseInt(pastedTagId))
        .filter(pastedTagId => !isNaN(pastedTagId))
        .map(pastedTagId => this.availableTags.find(tag => tag.id == pastedTagId))
        .filter(pastedTag => !!pastedTag)
        .map(pastedTag => pastedTag as Tag);
      for (let tag of tags) {
        this.addTag(tag);
      }
      this.notificationService.success(`Pasted tags`);
    }
  }
}
