import { BaseAttributeFilterDTO } from "./base-attribute-filter.dto";
import { EntryFilterDTO } from "./entry-filter.dto";

export interface RelationFilterDTO extends BaseAttributeFilterDTO {
    types?: string[];
    targetEntryFilters?: EntryFilterDTO[];
}