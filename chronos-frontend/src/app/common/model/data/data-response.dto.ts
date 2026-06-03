import { EntryDTO } from "./entry.dto";
import { DataResponseMetaInfoDTO } from "./data-response-meta-info.dto";

export interface DataResponseDTO {
    meta: DataResponseMetaInfoDTO;
    entries: EntryDTO[];
}