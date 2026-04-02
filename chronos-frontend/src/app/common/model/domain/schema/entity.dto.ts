import { AttributeDTO } from "./attribute.dto";

export interface EntityDTO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    attributes?: Array<AttributeDTO>;
}