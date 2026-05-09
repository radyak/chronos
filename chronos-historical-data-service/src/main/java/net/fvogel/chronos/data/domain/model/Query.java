package net.fvogel.chronos.data.domain.model;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Query {
    /**
     * Paging
     * (Start counting with 1 for more intuitive user behavior)
     */
    @Min(1)
    Integer page = 1;
    @Min(1)
    Integer pageSize = 10;

    /**
     * Sorting
     */
    SortOrder sortOrder = SortOrder.ASC;
    String sortBy;
}
