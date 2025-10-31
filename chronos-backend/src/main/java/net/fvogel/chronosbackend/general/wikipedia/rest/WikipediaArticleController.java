package net.fvogel.chronosbackend.general.wikipedia.rest;

import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.service.EntityService;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleSummary;
import net.fvogel.chronosbackend.general.wikipedia.service.WikipediaService;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
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
        return this.wikipediaService.findWikipediaSummaryByQid(randomEntity.getQid(), lang);
    }

}
