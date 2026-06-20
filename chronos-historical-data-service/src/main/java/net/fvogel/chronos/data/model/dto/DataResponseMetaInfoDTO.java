package net.fvogel.chronos.data.model.dto;

import lombok.Data;
import net.fvogel.chronos.data.model.ListQuery;

@Data
public class DataResponseMetaInfoDTO {
    String request;
    ListQuery query;
}
