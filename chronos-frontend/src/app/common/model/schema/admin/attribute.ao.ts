import { SchemaAttributeTypeDTO } from "../attribute-type.dto";

export interface SchemaAttributeAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    type?: SchemaAttributeTypeDTO;
    isMandatory?: boolean;
    isUnique?: boolean;
    isChangeable?: boolean;
    isArray?: boolean;
    order?: number;
    valuePattern?: string;
    valueRange?: string;
    allowedValues?: Array<string>;
}