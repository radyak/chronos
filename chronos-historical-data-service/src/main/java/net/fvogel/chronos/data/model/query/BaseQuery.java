package net.fvogel.chronos.data.model.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class BaseQuery {
    List<Filter> filters = new ArrayList<>();
}
