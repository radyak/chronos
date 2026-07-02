import { SchemaAttributeDTO } from "./attribute.dto";

export interface SchemaTypeDTO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    icon?: string;
    attributes?: Array<SchemaAttributeDTO>;
}