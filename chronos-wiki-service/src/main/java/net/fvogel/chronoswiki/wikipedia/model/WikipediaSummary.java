package net.fvogel.chronoswiki.wikipedia.model;

import lombok.Data;

@Data
public class WikipediaSummary {
    Integer pageid;
    String title;
    String extract;
    WikipediaImage image;
    String pageUrl;
}
