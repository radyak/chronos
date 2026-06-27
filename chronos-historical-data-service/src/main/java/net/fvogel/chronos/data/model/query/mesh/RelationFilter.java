package net.fvogel.chronos.data.model.query.mesh;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseAttributeFilter;
import net.fvogel.chronos.data.model.query.EntryFilter;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RelationFilter extends BaseAttributeFilter {
    List<String> types = new ArrayList<>();
    List<EntryFilter> targetEntryFilters = new ArrayList<>();
}
