package net.fvogel.chronosbackend.admin.tags.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.persistence.tags.repo.TagRepository;
import net.fvogel.chronosbackend.common.service.TagsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
public class AdminTagsController {

    private TagsService tagsService;

    public AdminTagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @PostMapping
    public Tag create(@Valid @RequestBody Tag tag) {
        return this.tagsService.save(tag);
    }

    @GetMapping
    public List<Tag> all() {
        return this.tagsService.findAll();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable("id") Long id) {
        return this.tagsService.findById(id);
    }

    @PutMapping
    public Tag update(@Valid @RequestBody Tag tag) {
        return this.tagsService.save(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        this.tagsService.deleteById(id);
    }

}
