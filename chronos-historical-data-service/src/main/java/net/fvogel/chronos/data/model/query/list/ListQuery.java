package net.fvogel.chronos.data.model.query.list;

import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;
import net.fvogel.chronos.data.model.query.Filter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListQuery extends BaseQuery {
    Pagination pagination = new Pagination();
    List<Sorting> sorting = new ArrayList<>();
}
