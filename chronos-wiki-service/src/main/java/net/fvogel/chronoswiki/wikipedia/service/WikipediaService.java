package net.fvogel.chronoswiki.wikipedia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronoswiki.common.exception.NotFoundException;
import net.fvogel.chronoswiki.wikipedia.dto.WikipediaPageDto;
import net.fvogel.chronoswiki.wikipedia.dto.WikipediaQueryResultDto;
import net.fvogel.chronoswiki.wikipedia.model.WikipediaImage;
import net.fvogel.chronoswiki.wikipedia.model.WikipediaSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WikipediaService {

    private RestClient restClient;
    private ObjectMapper objectMapper;

    public WikipediaService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public WikipediaSummary findWikipediaArticleSummary(String title) {
        String uri = "https://en.wikipedia.org/w/api.php?"
                + "action=query"
                + "&prop=extracts|pageimages|info"
                + "&inprop=url"
                + "&exsentences=10"
                + "&exlimit=1"
                + "&explaintext=1"
                + "&format=json"
                + "&pithumbsize=300"
                + "&titles=" + title;
        WikipediaQueryResultDto resultDto = this.restClient.get()
            .uri(uri)
            .retrieve()
            .body(WikipediaQueryResultDto.class);

        WikipediaPageDto wikipediaPageDto = resultDto.getQuery().getPages().entrySet().stream()
                .findFirst()
                .orElseThrow(NotFoundException::new)
                .getValue();

        WikipediaSummary wikipediaSummary = mapToWikipediaSummary(wikipediaPageDto);

        return wikipediaSummary;
    }

    private static WikipediaSummary mapToWikipediaSummary(WikipediaPageDto wikipediaPageDto) {
        WikipediaSummary wikipediaSummary = new WikipediaSummary();
        wikipediaSummary.setPageid(wikipediaPageDto.getPageid());
        wikipediaSummary.setTitle(wikipediaPageDto.getTitle());
        wikipediaSummary.setExtract(wikipediaPageDto.getExtract());
        wikipediaSummary.setPageUrl(wikipediaPageDto.getCanonicalurl());

        if (wikipediaPageDto.getThumbnail() != null) {
            WikipediaImage image = new WikipediaImage();
            wikipediaSummary.setImage(image);
            image.setUrl(wikipediaPageDto.getThumbnail().getSource());
            image.setHeight(wikipediaPageDto.getThumbnail().getHeight());
            image.setWidth(wikipediaPageDto.getThumbnail().getWidth());
        }
        return wikipediaSummary;
    }
}
