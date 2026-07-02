import { BaseAttributeFilterDTO } from "./base-attribute-filter.dto";

export interface EntryFilterDTO extends BaseAttributeFilterDTO {
    labels?: string[];
}
