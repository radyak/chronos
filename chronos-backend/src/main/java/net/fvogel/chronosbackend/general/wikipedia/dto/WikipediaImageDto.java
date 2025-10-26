package net.fvogel.chronosbackend.general.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaImageDto {
    private String source;
    private Integer width;
    private Integer height;
}
