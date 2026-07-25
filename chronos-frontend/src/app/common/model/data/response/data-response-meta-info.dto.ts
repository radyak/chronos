import { FilterOperator } from "../common/filter-operator.dto";
import { SortOrder } from "../common/sort-order.dto";

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
