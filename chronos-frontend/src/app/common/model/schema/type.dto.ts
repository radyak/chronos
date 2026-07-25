import { SchemaAttributeDTO } from "./attribute.dto";

export interface SchemaTypeDTO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    icon?: string;
    color?: string;
    attributes?: Array<SchemaAttributeDTO>;
}