import {Component, inject, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, NgIf} from "@angular/common";
import {Entry} from "../../../../model/entry.model";
import {TagSelectionComponent} from "../../../../ui-components/tag-selection/tag-selection.component";
import {Tag} from "../../../../model/tag.model";
import {AdminTagsService} from "../../services/admin-tags.service";


@Component({
  standalone: true,
  selector: 'chronos-admin-add-tags-modal',
  templateUrl: './admin-add-tags-modal.html',
  imports: [
    NgIf,
    TagSelectionComponent,
    AsyncPipe
  ],
  styleUrls: ['./admin-add-tags-modal.scss']
})
export class AdminAddTagsModal implements OnInit {

  activeModal = inject(NgbActiveModal);

  protected selectedTags: Array<Tag> = [];
  protected availableTags: Array<Tag> = [];

  @Input()
  selectedEntries: Array<Entry> = [];

  constructor(private tagsService: AdminTagsService) {
  }

  ngOnInit(): void {
      this.tagsService.allTags().subscribe(tags => this.availableTags = tags);
  }

  entriesString(): string {
    return this.selectedEntries.map(entry => entry.title).join(', ');
  }

}

