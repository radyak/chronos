package net.fvogel.chronos.data.model;

import lombok.Data;

@Data
public class Sorting {
    SortOrder sortOrder = SortOrder.ASC;
    String sortBy;
}
