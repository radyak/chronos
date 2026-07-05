import { SchemaAttributeAO } from "./attribute.ao";
import { SchemaRelationAO } from "./relation.ao";

export interface SchemaTypeAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    icon?: string;
    color?: string;
    attributes?: Array<SchemaAttributeAO>;
    defaultAttributes?: Array<SchemaAttributeAO>;
    relations?: Array<SchemaRelationAO>;
}