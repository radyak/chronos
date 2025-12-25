package net.fvogel.chronosbackend.commons.model.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute {
    Long id;
    String key;
    String label;
    String explanation;
    String examples;
    AttributeType type;
    Boolean isArray = false;
    String valuePattern;
    String valueRange;
    List<String> allowedValues = new ArrayList<>();
}
