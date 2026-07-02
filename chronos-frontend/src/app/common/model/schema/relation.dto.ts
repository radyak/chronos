import { SchemaAttributeDTO } from "./attribute.dto";

export interface SchemaRelationDTO {
    id: number;
    key: string;
    explanation: string;
    examples: string;
    attributes: Array<SchemaAttributeDTO>;
    defaultAttributes: Array<SchemaAttributeDTO>;
    sourceTypeId: number;
    targetTypeId: number;
}