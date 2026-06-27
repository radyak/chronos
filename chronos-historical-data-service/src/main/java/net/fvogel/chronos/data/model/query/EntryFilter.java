package net.fvogel.chronos.data.model.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EntryFilter {
    List<String> labels = new ArrayList<>();
    String attribute;
    ConditionOperator operator;
    String value;
}
