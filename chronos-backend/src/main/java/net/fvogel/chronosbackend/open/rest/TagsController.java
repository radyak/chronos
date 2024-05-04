package net.fvogel.chronosbackend.open.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.service.TagsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    private TagsService tagsService;

    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping
    public List<Tag> all() {
        return this.tagsService.findAll();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable("id") Long id) {
        return this.tagsService.findById(id);
    }

}
