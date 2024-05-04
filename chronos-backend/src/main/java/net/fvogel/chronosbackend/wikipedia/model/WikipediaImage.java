package net.fvogel.chronosbackend.wikipedia.model;

import lombok.Data;

@Data
public class WikipediaImage {
    String url;
    Integer width;
    Integer height;
}
