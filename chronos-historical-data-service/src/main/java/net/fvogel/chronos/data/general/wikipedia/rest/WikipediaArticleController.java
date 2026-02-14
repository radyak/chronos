package net.fvogel.chronos.data.general.wikipedia.rest;

import net.fvogel.chronos.commons.lang.SupportedLanguage;
import net.fvogel.chronos.data.domain.generic.persistence.Entity;
import net.fvogel.chronos.data.domain.generic.service.EntityService;
import net.fvogel.chronos.data.general.wikipedia.model.WikipediaArticleSummary;
import net.fvogel.chronos.data.general.wikipedia.service.WikipediaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wiki/articles")
public class WikipediaArticleController {

    private final WikipediaService wikipediaService;
    private final EntityService entityService;

    public WikipediaArticleController(WikipediaService wikipediaService,
                                      EntityService entityService) {
        this.wikipediaService = wikipediaService;
        this.entityService = entityService;
    }

    @GetMapping("/{id}")
    public WikipediaArticleSummary getWikipediaSummaryById(
            @PathVariable("id") String id,
            @RequestParam(name = "lang", required = false) SupportedLanguage lang) {
        return this.wikipediaService.findWikipediaSummaryByQid(id, lang);
    }

    @GetMapping("/random")
    public WikipediaArticleSummary getRandomWikipediaArticleSummary(
            @RequestParam(name = "lang", required = false) SupportedLanguage lang
    ) {
        Entity randomEntity = this.entityService.findRandomEntityWithQid();
        return this.wikipediaService.findWikipediaSummaryByQid(randomEntity.qid, lang);
    }

}
