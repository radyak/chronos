import { SchemaTypeAO } from "../admin/type.ao";
import { SchemaTypeDTO } from "../type.dto";
import { SchemaResponseDTO } from "../schema-response.dto";
import { AttributeMapper } from "./attribute.mapper";
import { RelationMapper } from "./relation.mapper";
import { sortByOrder } from "src/app/common/util/array-utils";

export class TypeMapper {

    public static fromSchemaResponseDTO(schemaPartial: SchemaResponseDTO): SchemaTypeAO {
        const root = schemaPartial.types.elements.find(
        el => el.key === schemaPartial.meta.base
        );
        if (!root) {
            console.log('Inconsistent response:', schemaPartial)
            throw new Error('Inconsistent response')
        }
        return TypeMapper.dtoToAo(root, schemaPartial);
    }

    public static dtoToAo(dto: SchemaTypeDTO, schemaPartial: SchemaResponseDTO): SchemaTypeAO {
        const ao: SchemaTypeAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            icon: dto.icon,
            color: dto.color,
            attributes: dto.attributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
            defaultAttributes: schemaPartial.types.defaultAttributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
            relations: schemaPartial.relations.elements.map(rel => RelationMapper.dtoToAo(rel, schemaPartial))
        };
        return ao;
    }

    public static onlyDefaults(schemaPartial: SchemaResponseDTO): SchemaTypeAO {
        const ao: SchemaTypeAO = {
            defaultAttributes: schemaPartial.types.defaultAttributes?.map(AttributeMapper.dtoToAo).sort(sortByOrder),
        };
        return ao;
    }

}