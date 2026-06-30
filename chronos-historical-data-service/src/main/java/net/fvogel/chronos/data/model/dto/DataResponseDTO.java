package net.fvogel.chronos.data.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.fvogel.chronos.data.model.internal.Entry;
import net.fvogel.chronos.data.model.internal.Relation;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponseDTO {
    DataResponseMetaInfoDTO meta = new DataResponseMetaInfoDTO();
    List<Entry> entries = new ArrayList<>();
    List<Relation> relations = new ArrayList<>();
}
