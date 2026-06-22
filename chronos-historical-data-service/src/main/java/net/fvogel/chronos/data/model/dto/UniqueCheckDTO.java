package net.fvogel.chronos.data.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UniqueCheckDTO {
    @NotEmpty
    String key;
    String value;
    String elementId;
}
