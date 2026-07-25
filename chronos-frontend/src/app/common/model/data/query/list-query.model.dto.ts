import { SortOrder } from "../common/sort-order.dto";

export interface ListQueryDTO {
    page?: number;
    pageSize?: number;
    sortOrder?: SortOrder;
    sortBy?: string;
}