package net.fvogel.chronos.data.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListQuery {
    List<Filter> filters = new ArrayList<>();
    Pagination pagination = new Pagination();
    List<Sorting> sorting = new ArrayList<>();
}
