import {Tag} from "../model/tag.model";

export function tagMatches(query: string, tag: Tag): boolean {
  if (!query) {
    return true;
  }
  return tag && `${tag.tagCategory?.name}: ${tag.name}`.toLowerCase().indexOf(query.toLowerCase()) > -1
}
