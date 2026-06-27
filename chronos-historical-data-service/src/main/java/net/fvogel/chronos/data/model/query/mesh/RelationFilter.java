package net.fvogel.chronos.data.model.query.mesh;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RelationFilter {
    List<String> types = new ArrayList<>();
    String attribute;
    ConditionOperator operator;
    String value;
    List<EntryFilter> targetEntryFilters = new ArrayList<>();
}
