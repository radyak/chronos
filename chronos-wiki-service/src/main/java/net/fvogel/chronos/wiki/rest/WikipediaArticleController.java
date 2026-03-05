package net.fvogel.chronos.wiki.rest;

import net.fvogel.chronos.commons.lang.SupportedLanguage;
import net.fvogel.chronos.wiki.model.WikipediaArticleSummary;
import net.fvogel.chronos.wiki.service.WikipediaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wiki/articles")
public class WikipediaArticleController {

    private final WikipediaService wikipediaService;

    public WikipediaArticleController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/{id}")
    public WikipediaArticleSummary getWikipediaSummaryById(
            @PathVariable("id") String id,
            @RequestParam(name = "lang", required = false) SupportedLanguage lang) {
        return this.wikipediaService.findWikipediaSummaryByQid(id, lang);
    }

}
