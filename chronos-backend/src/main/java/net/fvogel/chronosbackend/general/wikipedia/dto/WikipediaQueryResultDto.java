package net.fvogel.chronosbackend.general.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaQueryResultDto {
    String batchcomplete;
    WikipediaQueryDto query;
}
