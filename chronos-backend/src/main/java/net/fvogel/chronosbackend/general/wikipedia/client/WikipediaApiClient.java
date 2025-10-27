package net.fvogel.chronosbackend.general.wikipedia.client;

import net.fvogel.chronosbackend.general.wikipedia.dto.WikipediaQueryResultDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.getentities.WikipediaEntityResultDto;
import net.fvogel.chronosbackend.general.wikipedia.dto.search.WikipediaSearchResultDto;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WikipediaApiClient {

    private final RestClient restClient;
    private final String UserAgent = "Chronos/0.0";

    public WikipediaApiClient() {
        this.restClient = RestClient.create();
    }


    /**
     * Finds wikipedia articles with main data (title, thumbnail, QID - no content) by title (interpreted by language).
     *
     * @param title         The title or title part to search
     * @param lang          The language that the title is to be interpreted as & to search for
     * @param resultOffset  (optional) An offset, to start results at a later index (for pagination; wiki API returns 10 results by default)
     * @return              The search result DTO of wikipedia articles
     */
    public WikipediaSearchResultDto searchWikipediaArticleInfos(String title, SupportedLanguage lang, Integer resultOffset) {
        String uri = "https://" + lang + ".wikipedia.org/w/api.php?"
                + "action=query"
                + "&generator=search"
                + "&gsrsearch=" + title
                + "&format=json"
                + "&exlimit=1"
                + "&prop=pageimages|info|pageprops";
        if (resultOffset != null && resultOffset > 0) {
            uri += "&gsroffset=" + resultOffset;
        }
        return this.restClient.get()
                .uri(uri)
                .header("User-Agent", UserAgent)
                .retrieve()
                .body(WikipediaSearchResultDto.class);
    }


    /**
     * Loads a Wikipedia entity by QID
     *
     * @param qid   The QID
     * @param lang  The language; used as sitefilter
     * @return      The found entity
     */
    public WikipediaEntityResultDto getEntityByQid(String qid, SupportedLanguage lang) {
        String uri = "https://www.wikidata.org/w/api.php?"
                + "action=wbgetentities"
                + "&prop=sitelinks"
                + "&format=json"
                + "&ids=" + qid
                + "&sitefilter=" + lang + "wiki";

        return this.restClient.get()
                .uri(uri)
                .header("User-Agent", UserAgent)
                .retrieve()
                .body(WikipediaEntityResultDto.class);
    }


    /**
     * Loads a Wikipedia article by title
     * @param title     The title
     * @param lang      The language that the title is to be interpreted as & to search for
     * @return          The query result
     */
    public WikipediaQueryResultDto queryWikipediaArticleByTitle(String title, SupportedLanguage lang) {
        String uri = "https://" + lang + ".wikipedia.org/w/api.php?"
                + "action=query"
                + "&prop=extracts|pageimages|info"
                + "&inprop=url"
                + "&exsentences=10"
                + "&exlimit=1"
                + "&explaintext=1"
                + "&format=json"
                + "&pithumbsize=300"
                + "&titles=" + title;

        return this.restClient.get()
                .uri(uri)
                .header("User-Agent", UserAgent)
                .retrieve()
                .body(WikipediaQueryResultDto.class);
    }

}
