package net.fvogel.chronoswiki.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaQueryResultDto {
    String batchcomplete;
    WikipediaQueryDto query;
}
