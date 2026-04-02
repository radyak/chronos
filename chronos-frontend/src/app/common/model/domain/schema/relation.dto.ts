import { AttributeDTO } from "./attribute.dto";

export interface RelationDTO {
    id: number;
    key: string;
    explanation: string;
    examples: string;
    attributes: Array<AttributeDTO>;
    sourceEntityId: number;
    targetEntityId: number;
}