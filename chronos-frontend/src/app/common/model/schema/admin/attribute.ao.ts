import { AttributeTypeDTO } from "../attribute-type.dto";

export interface AttributeAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    type?: AttributeTypeDTO;
    isMandatory?: boolean;
    isUnique?: boolean;
    isChangeable?: boolean;
    isArray?: boolean;
    order?: number;
    valuePattern?: string;
    valueRange?: string;
    allowedValues?: Array<string>;
}