package net.fvogel.chronos.data.model;

import lombok.Data;

@Data
public class Filter {
    String attribute;
    ConditionOperator operator;
    String value;
}
