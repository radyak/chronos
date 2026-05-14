import { RelationAO } from "../admin/relation.ao";
import { RelationDTO } from "../relation.dto";
import { AttributeMapper } from "./attribute.mapper";
import { SchemaResponseDTO } from "../schema-response.dto";

export class RelationMapper {

    public static dtoToAo(dto: RelationDTO, schemaPartial: SchemaResponseDTO): RelationAO {
        const ao: RelationAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            attributes: dto.attributes?.map(AttributeMapper.dtoToAo),
            defaultAttributes: schemaPartial.relations.defaultAttributes?.map(AttributeMapper.dtoToAo),
            source: schemaPartial.types.elements.find(ent => ent.id === dto.sourceTypeId),
            target: schemaPartial.types.elements.find(ent => ent.id === dto.targetTypeId)
        };
        return ao;
    }

    public static onlyDefaults(schemaPartial: SchemaResponseDTO): RelationAO {
        const ao: RelationAO = {
            defaultAttributes: schemaPartial.relations.defaultAttributes?.map(AttributeMapper.dtoToAo),
        };
        return ao;
    }

}