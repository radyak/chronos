import { MetaInfoDTO } from "./meta-info.dto";

export interface EntryDTO {
    elementId: string;
    labels: string[];
    attributes: Record<string, any>;
    _meta?: MetaInfoDTO;
}