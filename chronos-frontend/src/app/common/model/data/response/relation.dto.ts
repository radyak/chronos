import { MetaInfoDTO } from "./meta-info.dto";

export interface RelationDTO {
    elementId: string;
    startElementId: string;
    endElementId: string;
    type: string;
    attributes: Record<string, any>;
    _meta?: MetaInfoDTO;
}