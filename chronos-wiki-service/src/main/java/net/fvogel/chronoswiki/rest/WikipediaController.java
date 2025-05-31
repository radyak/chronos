package net.fvogel.chronoswiki.rest;

import net.fvogel.chronoswiki.wikipedia.model.WikipediaSummary;
import net.fvogel.chronoswiki.wikipedia.service.WikipediaService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wiki")
public class WikipediaController {

    private WikipediaService wikipediaService;

    public WikipediaController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/search")
    public WikipediaSummary getWikipediaSummaryById(
        @RequestParam(name = "q", required = true) String search
    ) {
        return this.wikipediaService.findWikipediaArticleSummary(search);
    }

}
