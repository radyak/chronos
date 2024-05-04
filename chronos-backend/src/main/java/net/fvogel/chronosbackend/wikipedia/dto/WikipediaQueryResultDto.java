package net.fvogel.chronosbackend.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaQueryResultDto {
    String batchcomplete;
    WikipediaQueryDto query;
}
