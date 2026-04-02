import { AttributeDTO } from "./attribute.dto";
import { EntityDTO } from "./entity.dto";
import { RelationDTO } from "./relation.dto";

export interface SchemaResponseDTO {
    meta: {
        depth: number;
        query: string;
        base: string;
    };
    entities: {
        elements: Array<EntityDTO>;
        defaultAttributes: Array<AttributeDTO>;
    }
    relations: {
        elements: Array<RelationDTO>;
        defaultAttributes: Array<AttributeDTO>;
    }
}