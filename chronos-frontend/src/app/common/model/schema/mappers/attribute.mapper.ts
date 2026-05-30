import { AttributeAO } from "../admin/attribute.ao";
import { AttributeDTO } from "../attribute.dto";

export class AttributeMapper {

    public static dtoToAo(dto: AttributeDTO): AttributeAO {
        const ao: AttributeAO = {
            id: dto.id,
            key: dto.key,
            examples: dto.examples,
            explanation: dto.explanation,
            allowedValues: dto.allowedValues,
            isArray: dto.isArray,
            isMandatory: dto.isMandatory,
            type: dto.type,
            valuePattern: dto.valuePattern,
            valueRange: dto.valueRange,
        };
        return ao;
    }

}