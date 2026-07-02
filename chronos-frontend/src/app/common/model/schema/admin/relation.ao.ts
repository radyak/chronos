import { SchemaAttributeAO } from "./attribute.ao";
import { SchemaTypeAO } from "./type.ao";

export interface SchemaRelationAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    attributes?: Array<SchemaAttributeAO>;
    defaultAttributes?: Array<SchemaAttributeAO>;
    source?: SchemaTypeAO;
    target?: SchemaTypeAO;
}
