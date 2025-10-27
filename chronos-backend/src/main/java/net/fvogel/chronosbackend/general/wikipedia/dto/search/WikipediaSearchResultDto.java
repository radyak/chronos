package net.fvogel.chronosbackend.general.wikipedia.dto.search;

import lombok.Data;
import net.fvogel.chronosbackend.general.wikipedia.dto.WikipediaQueryDto;

@Data
public class WikipediaSearchResultDto {
    WikipediaQueryDto query;
}
