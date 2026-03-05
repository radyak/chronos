package net.fvogel.chronos.wiki.model;

import lombok.Data;

@Data
public class WikipediaArticleInfo {
    String title;
    String qid;
    WikipediaImage image;
}
