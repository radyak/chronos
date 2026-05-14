import { AttributeAO } from "./attribute.ao";
import { TypeAO } from "./type.ao";

export interface RelationAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    attributes?: Array<AttributeAO>;
    defaultAttributes?: Array<AttributeAO>;
    source?: TypeAO;
    target?: TypeAO;
}
