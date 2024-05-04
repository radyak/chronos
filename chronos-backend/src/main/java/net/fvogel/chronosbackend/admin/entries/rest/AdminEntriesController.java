package net.fvogel.chronosbackend.admin.entries.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.service.EntriesService;
import net.fvogel.chronosbackend.wikipedia.model.WikipediaSummary;
import net.fvogel.chronosbackend.wikipedia.service.WikipediaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/entries")
public class AdminEntriesController {

    private EntriesService entriesService;

    public AdminEntriesController(EntriesService entriesService) {
        this.entriesService = entriesService;
    }

    @PostMapping
    public Entry create(@Valid @RequestBody Entry entry) {
        return this.entriesService.save(entry);
    }

    @GetMapping
    public List<Entry> all() {
        return this.entriesService.findAll();
    }

    @GetMapping("wikipediasummary")
    public WikipediaSummary findWikipediasummary(@RequestParam(name = "title", required = false) String title) {
        return this.entriesService.findWikipediaSummaryForTitle(title);
    }

    @GetMapping("/{id}")
    public Entry getById(@PathVariable("id") Long id) {
        return this.entriesService.findById(id);
    }

    @PutMapping
    public Entry update(@Valid @RequestBody Entry entry) {
        return this.entriesService.save(entry);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        this.entriesService.deleteById(id);
    }

}
