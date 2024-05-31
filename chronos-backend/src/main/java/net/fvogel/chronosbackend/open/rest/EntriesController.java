package net.fvogel.chronosbackend.open.rest;

import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.service.EntriesService;
import net.fvogel.chronosbackend.wikipedia.model.WikipediaSummary;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/entries")
public class EntriesController {

    protected EntriesService entriesService;

    public EntriesController(EntriesService entriesService) {
        this.entriesService = entriesService;
    }

    @GetMapping
    public List<Entry> all(
            @RequestParam(name = "ids", required = false) Long[] idArray,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "tags", required = false) Long[] tagIdArray,
            @RequestParam(name = "from", required = false) Short from,
            @RequestParam(name = "to", required = false) Short to
    ) {
        Set<Long> ids = idArray != null ? new HashSet<>(Arrays.asList(idArray)) : null;
        Set<Long> tagIds = tagIdArray != null ? new HashSet<>(Arrays.asList(tagIdArray)) : null;
        return this.entriesService.find(ids, title, tagIds, from, to);
    }

    @GetMapping("/{id}")
    public Entry getById(@PathVariable("id") Long id) {
        return this.entriesService.findById(id);
    }

    @GetMapping("/random/wikipediasummary")
    public WikipediaSummary getRandom() {
        return this.entriesService.findRandom();
    }

    @GetMapping("/{id}/wikipediasummary")
    public WikipediaSummary getWikipediaSummaryById(@PathVariable("id") Long id) {
        return this.entriesService.findWikipediaSummaryForEntry(id);
    }

}
