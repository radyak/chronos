package net.fvogel.chronos.data.model.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class BaseAttributeFilter {
    String attribute;
    ConditionOperator operator;
    String value;
}
