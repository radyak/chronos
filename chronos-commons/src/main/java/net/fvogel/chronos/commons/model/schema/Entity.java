package net.fvogel.chronos.commons.model.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity {
    Long id;
    String key;
    String explanation;
    String examples;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Attribute> attributes = new ArrayList<>();
}
