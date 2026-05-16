import { AttributeDTO } from "./attribute.dto";

export interface TypeDTO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    icon?: string;
    attributes?: Array<AttributeDTO>;
}