package net.fvogel.chronos.data.model.query.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ListQuery extends BaseQuery {
    Pagination pagination = new Pagination();
    List<Sorting> sorting = new ArrayList<>();
}
