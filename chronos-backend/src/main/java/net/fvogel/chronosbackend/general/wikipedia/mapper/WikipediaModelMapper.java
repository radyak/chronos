package net.fvogel.chronosbackend.general.wikipedia.mapper;

import net.fvogel.chronosbackend.general.wikipedia.dto.WikipediaPageDto;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleInfo;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleSummary;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaImage;
import org.springframework.stereotype.Service;

@Service
public class WikipediaModelMapper {

    public static WikipediaArticleSummary mapToWikipediaSummary(WikipediaPageDto wikipediaPageDto) {
        WikipediaArticleSummary wikipediaArticleSummary = new WikipediaArticleSummary();
        wikipediaArticleSummary.setPageid(wikipediaPageDto.getPageid());
        wikipediaArticleSummary.setTitle(wikipediaPageDto.getTitle());
        wikipediaArticleSummary.setExtract(wikipediaPageDto.getExtract());
        wikipediaArticleSummary.setPageUrl(wikipediaPageDto.getCanonicalurl());

        if (wikipediaPageDto.getThumbnail() != null) {
            WikipediaImage image = new WikipediaImage();
            wikipediaArticleSummary.setImage(image);
            image.setUrl(wikipediaPageDto.getThumbnail().getSource());
            image.setHeight(wikipediaPageDto.getThumbnail().getHeight());
            image.setWidth(wikipediaPageDto.getThumbnail().getWidth());
        }
        return wikipediaArticleSummary;
    }

    public static WikipediaArticleInfo mapToWikipediaShortResult(WikipediaPageDto pageDto) {
        WikipediaArticleInfo result = new WikipediaArticleInfo();
        result.setTitle(pageDto.getTitle());

        if (pageDto.getPageprops() != null) {
            result.setQid(pageDto.getPageprops().getWikibase_item());
        }

        if (pageDto.getThumbnail() != null) {
            WikipediaImage image = new WikipediaImage();
            result.setImage(image);
            image.setUrl(pageDto.getThumbnail().getSource());
            image.setHeight(pageDto.getThumbnail().getHeight());
            image.setWidth(pageDto.getThumbnail().getWidth());
        }
        return result;
    }
}
