package net.fvogel.chronos.wiki.testutils;

import net.fvogel.chronos.wiki.dto.WikipediaImageDto;
import net.fvogel.chronos.wiki.dto.WikipediaPageDto;
import net.fvogel.chronos.wiki.dto.WikipediaQueryDto;
import net.fvogel.chronos.wiki.dto.WikipediaQueryResultDto;
import net.fvogel.chronos.wiki.dto.getentities.WikipediaEntityDto;
import net.fvogel.chronos.wiki.dto.getentities.WikipediaEntityResultDto;
import net.fvogel.chronos.wiki.dto.getentities.WikipediaSiteLinkDto;
import net.fvogel.chronos.wiki.dto.search.WikipediaSearchResultDto;

import java.util.HashMap;
import java.util.Map;

public class WikiArticleTestData {

    public static WikipediaEntityResultDto defaultWikipediaEntityResultDto() {
        WikipediaEntityResultDto wikipediaEntityResultDto = new WikipediaEntityResultDto();
        Map<String, WikipediaEntityDto> entityDtoMap = new HashMap<>();
        WikipediaEntityDto wikipediaEntityDto = new WikipediaEntityDto();
        wikipediaEntityDto.setId("Q1234");
        wikipediaEntityDto.setType("type");
        Map<String, WikipediaSiteLinkDto> siteLinks = new HashMap<>();
        WikipediaSiteLinkDto enLink = new WikipediaSiteLinkDto();
        enLink.setSite("ensite");
        enLink.setTitle("Otho");
        siteLinks.put("enwiki", enLink);
        WikipediaSiteLinkDto itLink = new WikipediaSiteLinkDto();
        itLink.setSite("itsite");
        itLink.setTitle("Othone");
        siteLinks.put("itwiki", itLink);
        wikipediaEntityDto.setSitelinks(siteLinks);
        entityDtoMap.put("Q1234", wikipediaEntityDto);
        wikipediaEntityResultDto.setEntities(entityDtoMap);
        return wikipediaEntityResultDto;
    }

    public static WikipediaQueryResultDto defaultWikipediaQueryResultDto() {
        WikipediaQueryResultDto wikipediaQueryResultDto = new WikipediaQueryResultDto();
        wikipediaQueryResultDto.setQuery(defaultWikipediaQueryDto());

        return wikipediaQueryResultDto;
    }

    public static WikipediaSearchResultDto defaultWikipediaSearchResultDto() {
        WikipediaSearchResultDto wikipediaSearchResultDto = new WikipediaSearchResultDto();
        wikipediaSearchResultDto.setQuery(defaultWikipediaQueryDto());
        return wikipediaSearchResultDto;
    }

    private static WikipediaQueryDto defaultWikipediaQueryDto() {
        WikipediaQueryDto wikipediaQueryDto = new WikipediaQueryDto();
        Map<String, WikipediaPageDto> pages = new HashMap<>();
        WikipediaPageDto wikipediaPageDto = new WikipediaPageDto();
        wikipediaPageDto.setPageid(9999);
        wikipediaPageDto.setTitle("Otho");
        wikipediaPageDto.setExtract("Otho was ...");
        wikipediaPageDto.setCanonicalurl("https://wiki/otho");
        WikipediaImageDto wikipediaImageDto = new WikipediaImageDto();
        wikipediaImageDto.setHeight(100);
        wikipediaImageDto.setWidth(200);
        wikipediaImageDto.setSource("https://wiki/otho/otho.jpg");
        wikipediaPageDto.setThumbnail(wikipediaImageDto);
        pages.put("someId", wikipediaPageDto);
        wikipediaQueryDto.setPages(pages);

        return wikipediaQueryDto;
    }

}
