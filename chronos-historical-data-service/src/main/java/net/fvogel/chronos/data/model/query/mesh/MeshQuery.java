package net.fvogel.chronos.data.model.query.mesh;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MeshQuery extends BaseQuery {
    List<RelationFilter> relationFilters = new ArrayList<>();
}
