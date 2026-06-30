package net.fvogel.chronos.data.model.query.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Sorting {
    SortOrder sortOrder = SortOrder.ASC;
    String sortBy;
}
