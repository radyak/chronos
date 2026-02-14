package net.fvogel.chronos.data.general.wikipedia.model;

import lombok.Data;

@Data
public class WikipediaArticleSummary {
    Integer pageid;
    String title;
    String extract;
    WikipediaImage image;
    String pageUrl;
}
