package net.fvogel.chronos.data.general.wikipedia.dto.search;

import lombok.Data;
import net.fvogel.chronos.data.general.wikipedia.dto.WikipediaQueryDto;

@Data
public class WikipediaSearchResultDto {
    WikipediaQueryDto query;
}
