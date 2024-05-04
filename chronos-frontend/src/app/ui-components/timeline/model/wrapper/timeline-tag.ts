import {Tag} from "../../../../model/tag.model";

export interface TimelineTag {
  name?: string;
  color?: string;
  category?: string;
  icon?: string;
  original: Tag;
}
