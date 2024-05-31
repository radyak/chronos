package net.fvogel.chronosbackend.admin.entries.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.service.EntriesService;
import net.fvogel.chronosbackend.open.rest.EntriesController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/entries")
public class AdminEntriesController extends EntriesController {

    public AdminEntriesController(EntriesService entriesService) {
        super(entriesService);
    }

    @PostMapping
    public Entry create(@Valid @RequestBody Entry entry) {
        return this.entriesService.save(entry);
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
