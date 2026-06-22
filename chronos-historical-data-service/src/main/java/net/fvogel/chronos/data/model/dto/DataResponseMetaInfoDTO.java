package net.fvogel.chronos.data.model.dto;

import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;

@Data
public class DataResponseMetaInfoDTO {
    String request;
    BaseQuery query;
}
