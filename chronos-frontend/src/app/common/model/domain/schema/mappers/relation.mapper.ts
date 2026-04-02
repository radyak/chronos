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
            attributes: [
                ...dto.attributes?.map(AttributeMapper.dtoToAo),
                ...schemaPartial.relations.defaultAttributes?.map(AttributeMapper.dtoToAo) ?? [],
            ],
            source: schemaPartial.entities.elements.find(ent => ent.id === dto.sourceEntityId),
            target: schemaPartial.entities.elements.find(ent => ent.id === dto.targetEntityId)
        };
        return ao;
    }

}