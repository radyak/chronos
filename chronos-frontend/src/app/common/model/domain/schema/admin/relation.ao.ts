import { AttributeAO } from "./attribute.ao";
import { EntityAO } from "./entity.ao";

export interface RelationAO {
    id?: number;
    key?: string;
    explanation?: string;
    examples?: string;
    attributes?: Array<AttributeAO>;
    defaultAttributes?: Array<AttributeAO>;
    source?: EntityAO;
    target?: EntityAO;
}
