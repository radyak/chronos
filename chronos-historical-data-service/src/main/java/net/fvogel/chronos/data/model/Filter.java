package net.fvogel.chronos.data.model;

import lombok.Data;
import net.fvogel.chronos.data.service.ConditionOperator;

@Data
public class Filter {
    String attribute;
    ConditionOperator operator;
    String value;
}
