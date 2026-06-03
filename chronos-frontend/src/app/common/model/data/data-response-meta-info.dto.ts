import { SortOrder } from "./sort-order.dto";

export interface DataResponseMetaInfoDTO {
    request: string;
    query: {
        filters: FilterDTO[];
        pagination: PaginationDTO;
        sorting: SortingDTO[];
    };
}


interface SortingDTO {
    sortOrder: SortOrder;
    sortBy: string;
}

interface PaginationDTO {
    page: number;
    pageSize: number;
}

interface FilterDTO {
    attribute: string;
    operator: FilterOperator;
    value: string;
}

enum FilterOperator {
    EQUAL = "EQUAL",
    NOT = "NOT",
    GREATER_THAN = "GREATER_THAN",
    GREATER_EQUAL_THAN = "GREATER_EQUAL_THAN",
    LESS_THAN = "LESS_THAN",
    LESS_EQUAL_THAN = "LESS_EQUAL_THAN",
    // CONTAINS = "CONTAINS", Good idea, AI?
}