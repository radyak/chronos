import { SchemaTypeAO } from "src/app/common/model/schema/admin/type.ao";

export class EditTypeFormMapper {
    public static toAO(formData: any, original?: SchemaTypeAO): SchemaTypeAO {
        return {
            ...original,
            key: formData.key,
            examples: formData.examples,
            explanation: formData.explanation,
            icon: formData.icon
        };
    }
}