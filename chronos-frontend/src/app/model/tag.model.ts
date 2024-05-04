import {TagCategory} from "./tag-category.model";

export interface Tag {
  id?: number;
  name?: string;
  color?: string;
  tagCategory?: TagCategory;
}
