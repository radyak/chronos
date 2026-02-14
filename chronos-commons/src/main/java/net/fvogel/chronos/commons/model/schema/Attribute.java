package net.fvogel.chronos.commons.model.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute {
    Long id;
    String key;
    String explanation;
    String examples;
    AttributeType type;
    Boolean isArray = false;
    Boolean isMandatory = false;
    String valuePattern;
    String valueRange;
    Set<String> allowedValues;
}
