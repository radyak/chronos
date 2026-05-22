package net.fvogel.chronos.data.model.dto;

import lombok.Data;
import net.fvogel.chronos.data.model.Entry;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataResponseDTO {
    DataResponseMetaInfoDTO meta = new DataResponseMetaInfoDTO();
    List<Entry> entries = new ArrayList<>();
    // TODO: Add relations
}
