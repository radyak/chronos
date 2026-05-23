import { EntryDTO } from "./data-element.dto";
import { DataResponseMetaInfoDTO } from "./data-response.dto copy";

export interface DataResponseDTO {
    meta: DataResponseMetaInfoDTO;
    entries: EntryDTO[];
}