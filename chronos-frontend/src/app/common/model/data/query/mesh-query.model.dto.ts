import { EntryFilterDTO } from "./filter/entry-filter.dto";
import { RelationFilterDTO } from "./filter/relation-filter.dto";

export interface MeshQueryDTO {
    entryFilters?: EntryFilterDTO[];
    relationFilters?: RelationFilterDTO[];
}