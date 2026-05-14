import { TypeAO } from "src/app/common/model/domain/schema/admin/type.ao";

export class EditTypeFormMapper {
    public static toAO(formData: any, original?: TypeAO): TypeAO {
        return {
            ...original,
            key: formData.key,
            examples: formData.examples,
            explanation: formData.explanation,
            icon: formData.icon
        };
    }
}