import { SchemaRelationAO } from "../admin/relation.ao";
import { SchemaRelationDTO } from "../relation.dto";
import { AttributeMapper } from "./attribute.mapper";
import { SchemaResponseDTO } from "../schema-response.dto";
import { sortByOrder } from "src/app/common/util/array-utils";

export class RelationMapper {

    public static dtoToAo(dto: SchemaRelationDTO, schemaPartial: SchemaResponseDTO): SchemaRelationAO {
        const ao: SchemaRelationAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            attributes: dto.attributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
            defaultAttributes: schemaPartial.relations.defaultAttributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
            source: schemaPartial.types.elements.find(ent => ent.id === dto.sourceTypeId),
            target: schemaPartial.types.elements.find(ent => ent.id === dto.targetTypeId)
        };
        return ao;
    }

    public static onlyDefaults(schemaPartial: SchemaResponseDTO): SchemaRelationAO {
        const ao: SchemaRelationAO = {
            defaultAttributes: schemaPartial.relations.defaultAttributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
        };
        return ao;
    }

}