package net.fvogel.chronosbackend.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaImageDto {
    private String source;
    private Integer width;
    private Integer height;
}
