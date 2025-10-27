package net.fvogel.chronosbackend.general.wikipedia.model;

import lombok.Data;

@Data
public class WikipediaArticleInfo {
    String title;
    String qid;
    WikipediaImage image;
}
