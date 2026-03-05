package net.fvogel.chronos.wiki.model;

import lombok.Data;

@Data
public class WikipediaArticleSummary {
    Integer pageid;
    String title;
    String extract;
    WikipediaImage image;
    String pageUrl;
}
