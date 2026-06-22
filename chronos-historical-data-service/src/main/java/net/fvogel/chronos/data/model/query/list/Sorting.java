package net.fvogel.chronos.data.model.query.list;

import lombok.Data;

@Data
public class Sorting {
    SortOrder sortOrder = SortOrder.ASC;
    String sortBy;
}
