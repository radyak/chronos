package net.fvogel.chronosbackend.general.wikipedia.service;

import net.fvogel.chronosbackend.config.caching.CachingConfig;
import net.fvogel.chronosbackend.general.wikipedia.client.WikipediaApiClient;
import net.fvogel.chronosbackend.general.wikipedia.dto.WikipediaPageDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.WikipediaQueryResultDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.getentities.WikipediaEntityDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.getentities.WikipediaEntityResultDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.getentities.WikipediaSiteLinkDto;
import net.fvogel.chronosbackend.general.wikipedia.mapper.WikipediaModelMapper;
import net.fvogel.chronosbackend.general.wikipedia.dto.search.WikipediaSearchResultDto;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleSummary;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleInfo;
import net.fvogel.chronosbackend.shared.exception.InvalidParameterException;
import net.fvogel.chronosbackend.shared.exception.NotFoundException;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WikipediaService {

    private WikipediaApiClient apiClient;

    public WikipediaService(WikipediaApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Finds Wikipedia article infos (containing title, thumbnail, QID) by title (interpreted by language).
     *
     * @param title         The title or title part to search; must be at least 3 characters long
     * @param lang          (optional; default: "en") The language that the title is to be interpreted as & to search for
     * @param offset        (optional) An offset, to start results at a later index (for pagination; wiki API returns 10 results by default)
     * @return              A list of wikipedia articles
     */
    @Cacheable(CachingConfig.CACHE_NAME_WIKI_ARTICLES_SEARCH)
    public List<WikipediaArticleInfo> searchWikipediaArticlesByTitle(String title, SupportedLanguage lang, Integer offset) {
        System.out.println("Searching for title " + title + ", lang " + lang + ", offset " + offset);
        if (title == null || title.length() < 3) {
            throw new InvalidParameterException();
        }
        if (lang == null) {
            lang = SupportedLanguage.EN;
        }

        WikipediaSearchResultDto resultDto = this.apiClient.searchWikipediaArticleInfos(title, lang, offset);

        if (resultDto == null || resultDto.getQuery() == null || resultDto.getQuery().getPages() == null) {
            return Collections.emptyList();
        }
        return resultDto.getQuery().getPages().values().stream()
                .map(WikipediaModelMapper::mapToWikipediaShortResult)
                .toList();
    }

    /**
     * Loads a Wikipedia article
     * @param qid
     * @param lang  (optional; default: "en") The language that the title is to be interpreted as & to search for
     * @return      The Wikipedia article
     */
    @Cacheable(CachingConfig.CACHE_NAME_WIKI_ARTICLE)
    public WikipediaArticleSummary findWikipediaSummaryByQid(String qid, SupportedLanguage lang) {
        System.out.println("Loading for ID " + qid + ", lang " + lang);
        if (qid == null || !qid.startsWith("Q")) {
            throw new InvalidParameterException();
        }
        if (lang == null) {
            lang = SupportedLanguage.EN;
        }
        WikipediaEntityResultDto entityByQid = this.apiClient.getEntityByQid(qid, lang);
        try {
            WikipediaEntityDto entityDto = entityByQid.getEntities().get(qid);
            WikipediaSiteLinkDto siteLinkDto = entityDto.getSitelinks().get(lang + "wiki");

            WikipediaQueryResultDto queryResultDto = this.apiClient.queryWikipediaArticleByTitle(siteLinkDto.getTitle(), lang);

            WikipediaPageDto wikipediaPageDto = queryResultDto.getQuery().getPages().entrySet().stream()
                    .findFirst()
                    .orElseThrow(NotFoundException::new)
                    .getValue();

            return WikipediaModelMapper.mapToWikipediaSummary(wikipediaPageDto);
        } catch (NullPointerException e) {
            throw new NotFoundException();
        }
    }

}
