import { SortOrder } from "./sort-order.dto";

export interface DataResponseMetaInfoDTO {
    request: string;
    query: {
        filters: FilterDTO[];
        pagination: PaginationDTO;
        sorting: SortingDTO[];
    };
}


export interface SortingDTO {
    sortOrder: SortOrder;
    sortBy: string;
}

export interface PaginationDTO {
    page: number;
    pageSize: number;
}

export interface FilterDTO {
    attribute: string;
    operator: FilterOperator;
    value: string;
}

export enum FilterOperator {
    EQUAL = "EQUAL",
    NOT = "NOT",
    GREATER_THAN = "GREATER_THAN",
    GREATER_EQUAL_THAN = "GREATER_EQUAL_THAN",
    LESS_THAN = "LESS_THAN",
    LESS_EQUAL_THAN = "LESS_EQUAL_THAN",
    // CONTAINS = "CONTAINS", Good idea, AI?
}