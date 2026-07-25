import { FilterOperator } from "../../common/filter-operator.dto";

export interface BaseAttributeFilterDTO {
    attribute?: string;
    operator?: FilterOperator;
    value?: string;
}
