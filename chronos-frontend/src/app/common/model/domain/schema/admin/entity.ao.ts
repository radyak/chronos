import { AttributeAO } from "./attribute.ao";
import { RelationAO } from "./relation.ao";

export interface EntityAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    icon?: string;
    attributes?: Array<AttributeAO>;
    defaultAttributes?: Array<AttributeAO>;
    relations?: Array<RelationAO>;
}