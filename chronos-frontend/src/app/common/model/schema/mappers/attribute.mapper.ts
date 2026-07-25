import { SchemaAttributeAO } from "../admin/attribute.ao";
import { SchemaAttributeDTO } from "../attribute.dto";

export class AttributeMapper {

    public static dtoToAo(dto: SchemaAttributeDTO): SchemaAttributeAO {
        const ao: SchemaAttributeAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            allowedValues: dto.allowedValues,
            isMandatory: dto.isMandatory,
            isUnique: dto.isUnique,
            isChangeable: dto.isChangeable,
            isArray: dto.isArray,
            order: dto.order,
            type: dto.type,
            valuePattern: dto.valuePattern,
            valueRange: dto.valueRange,
        };
        return ao;
    }

}