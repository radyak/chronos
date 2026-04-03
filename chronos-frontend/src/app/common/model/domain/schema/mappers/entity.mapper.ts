import { EntityAO } from "../admin/entity.ao";
import { EntityDTO } from "../entity.dto";
import { SchemaResponseDTO } from "../schema-response.dto";
import { AttributeMapper } from "./attribute.mapper";
import { RelationMapper } from "./relation.mapper";

export class EntityMapper {

    public static fromSchemaResponseDTO(schemaPartial: SchemaResponseDTO): EntityAO {
        const root = schemaPartial.entities.elements.find(
        el => el.key === schemaPartial.meta.base
        );
        if (!root) {
            console.log('Inconsistent response:', schemaPartial)
            throw new Error('Inconsistent response')
        }
        return EntityMapper.dtoToAo(root, schemaPartial);
    }

    public static dtoToAo(dto: EntityDTO, schemaPartial: SchemaResponseDTO): EntityAO {
        const ao: EntityAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            attributes: dto.attributes?.map(AttributeMapper.dtoToAo),
            defaultAttributes: schemaPartial.entities.defaultAttributes?.map(AttributeMapper.dtoToAo),
            relations: schemaPartial.relations.elements.map(rel => RelationMapper.dtoToAo(rel, schemaPartial))
        };
        return ao;
    }

}