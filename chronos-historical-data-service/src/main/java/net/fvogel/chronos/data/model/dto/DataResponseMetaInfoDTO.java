package net.fvogel.chronos.data.model.dto;

import lombok.Data;
import net.fvogel.chronos.data.model.DataQuery;

@Data
public class DataResponseMetaInfoDTO {
    String request;
    DataQuery query;
}
