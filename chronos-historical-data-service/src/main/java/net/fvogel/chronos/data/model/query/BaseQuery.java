package net.fvogel.chronos.data.model.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseQuery {
    List<Filter> filters = new ArrayList<>();
}
