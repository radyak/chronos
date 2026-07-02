import { EntryDTO } from "./entry.dto";
import { DataResponseMetaInfoDTO } from "./data-response-meta-info.dto";
import { RelationDTO } from "./relation.dto";

export interface DataResponseDTO {
    meta: DataResponseMetaInfoDTO;
    entries: EntryDTO[];
    relations: RelationDTO[];
}