package net.fvogel.chronos.data.model;

import lombok.Data;

import java.util.Map;

@Data
public class DataQuery {
    Sorting sorting = new Sorting();
    Pagination pagination = new Pagination();
    Map<String, String> filters;
}
