package net.fvogel.chronosbackend.general.admin.rest;

import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaArticleInfo;
import net.fvogel.chronosbackend.general.wikipedia.service.WikipediaService;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wiki/articles")
public class AdminWikipediaArticleController {

    private final WikipediaService wikipediaService;

    public AdminWikipediaArticleController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/search")
    public List<WikipediaArticleInfo> searchArticles(
            @RequestParam("q") String titleQuery,
            @RequestParam(name = "lang", required = false) SupportedLanguage lang,
            @RequestParam(name = "offset", required = false) Integer offset
    ) {
        return this.wikipediaService.searchWikipediaArticlesByTitle(titleQuery, lang, offset);
    }

}
