import { AttributeDTO } from "./attribute.dto";
import { TypeDTO } from "./type.dto";
import { RelationDTO } from "./relation.dto";

export interface SchemaResponseDTO {
    meta: {
        depth: number;
        query: string;
        base: string;
    };
    types: {
        elements: Array<TypeDTO>;
        defaultAttributes: Array<AttributeDTO>;
    }
    relations: {
        elements: Array<RelationDTO>;
        defaultAttributes: Array<AttributeDTO>;
    }
}