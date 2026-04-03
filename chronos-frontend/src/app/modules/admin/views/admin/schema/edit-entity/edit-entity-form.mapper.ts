import { EntityAO } from "src/app/common/model/domain/schema/admin/entity.ao";

export class EditEntityFormMapper {
    public static toAO(formData: any, original?: EntityAO): EntityAO {
        return {
            ...original,
            key: formData.key,
            examples: formData.examples,
            explanation: formData.explanation
        };
    }
}