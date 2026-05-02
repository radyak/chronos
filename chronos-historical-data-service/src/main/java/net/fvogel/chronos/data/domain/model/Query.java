package net.fvogel.chronos.data.domain.model;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Query {
    /**
     * Paging
     */
    @Min(0)
    Integer page = 0;
    @Min(1)
    Integer pageSize = 10;

    /**
     * Sorting
     */
    Boolean sortAsc = true;
    String sortBy;
}
