import { SchemaAttributeDTO } from "./attribute.dto";
import { SchemaTypeDTO } from "./type.dto";
import { SchemaRelationDTO } from "./relation.dto";

export interface SchemaResponseDTO {
    meta: {
        depth: number;
        query: string;
        base: string;
    };
    types: {
        elements: Array<SchemaTypeDTO>;
        defaultAttributes: Array<SchemaAttributeDTO>;
    }
    relations: {
        elements: Array<SchemaRelationDTO>;
        defaultAttributes: Array<SchemaAttributeDTO>;
    }
}