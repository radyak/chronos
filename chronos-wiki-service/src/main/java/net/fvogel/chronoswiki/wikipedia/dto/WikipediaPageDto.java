package net.fvogel.chronoswiki.wikipedia.dto;

import lombok.Data;

@Data
public class WikipediaPageDto {
    Integer pageid;
    Integer ns;
    String title;
    String extract;
    WikipediaImageDto thumbnail;
    String pageimage;
    String canonicalurl;
}
