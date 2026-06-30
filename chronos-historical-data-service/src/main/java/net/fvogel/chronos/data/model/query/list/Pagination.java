package net.fvogel.chronos.data.model.query.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Pagination {
    @Min(1)
    Integer page = 1;
    @Min(1)
    Integer pageSize = 10;
}
