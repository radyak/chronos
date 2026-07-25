import { EntryDTO } from "src/app/common/model/data/response/entry.dto";
import { RelationDTO } from "src/app/common/model/data/response/relation.dto";

export interface EntityNetworkGraphData {
    entries: EntryDTO[];
    relations: RelationDTO[];
}