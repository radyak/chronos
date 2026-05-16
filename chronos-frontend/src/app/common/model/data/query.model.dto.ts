import { SortOrder } from "./sort-order.dto";

export interface QueryDTO {
    page?: number;
    pageSize?: number;
    sortOrder?: SortOrder;
    sortBy?: string;
}