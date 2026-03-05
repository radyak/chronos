package net.fvogel.chronos.wiki.dto.search;

import lombok.Data;
import net.fvogel.chronos.wiki.dto.WikipediaQueryDto;

@Data
public class WikipediaSearchResultDto {
    WikipediaQueryDto query;
}
