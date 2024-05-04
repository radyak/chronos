import {Component, OnInit} from '@angular/core';
import {AdminTagCategoriesService} from "../../services/admin-tag-categories.service";
import {Observable, of} from "rxjs";
import {TagCategory} from "../../../../model/tag-category.model";
import {faPenToSquare, faPlus, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {Tag} from "../../../../model/tag.model";
import {AdminTagsService} from "../../services/admin-tags.service";

@Component({
  selector: 'chronos-admin-tags',
  templateUrl: './admin-tags.component.html',
  styleUrls: ['./admin-tags.component.scss']
})
export class AdminTagsComponent {

}
