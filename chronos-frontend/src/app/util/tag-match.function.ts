import {Tag} from "../model/tag.model";

export function tagMatches(query: string, tag: Tag): boolean {
  return tag && `${tag.tagCategory?.name}: ${tag.name}`.toLowerCase().indexOf(query.toLowerCase()) > -1
}
