import { AttributeTypeDTO } from "../attribute-type.dto";

export interface AttributeAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    type?: AttributeTypeDTO;
    isArray?: boolean;
    isMandatory?: boolean;
    valuePattern?: string;
    valueRange?: string;
    allowedValues?: Array<string>;
}