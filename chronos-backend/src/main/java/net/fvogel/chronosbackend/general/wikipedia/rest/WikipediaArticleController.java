package net.fvogel.chronosbackend.general.wikipedia.rest;

import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleSummary;
import net.fvogel.chronosbackend.general.wikipedia.service.WikipediaService;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wiki/articles")
public class WikipediaArticleController {

    private WikipediaService wikipediaService;

    public WikipediaArticleController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/{id}")
    public WikipediaArticleSummary getWikipediaSummaryById(
            @PathVariable("id") String id,
            @RequestParam(name = "lang", required = false) SupportedLanguage lang) {
        return this.wikipediaService.findWikipediaSummaryByQid(id, lang);
    }

//    @GetMapping("/random")
//    public WikipediaSummary getRandom() {
//        // TODO: Implement
//        return this.personsService.findRandom();
//    }

}
