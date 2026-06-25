package net.fvogel.chronos.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataResponseMetaInfoDTO {
    String request;
    BaseQuery query;
}
