package net.fvogel.chronos.data.model;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Pagination {
    @Min(1)
    Integer page = 1;
    @Min(1)
    Integer pageSize = 10;
}
