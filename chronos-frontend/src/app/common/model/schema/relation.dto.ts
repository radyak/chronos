import { AttributeDTO } from "./attribute.dto";

export interface RelationDTO {
    id: number;
    key: string;
    explanation: string;
    examples: string;
    attributes: Array<AttributeDTO>;
    defaultAttributes: Array<AttributeDTO>;
    sourceTypeId: number;
    targetTypeId: number;
}