package net.fvogel.chronosbackend.admin.tags.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.persistence.tags.repo.TagRepository;
import net.fvogel.chronosbackend.common.service.TagsService;
import net.fvogel.chronosbackend.open.rest.TagsController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
public class AdminTagsController extends TagsController {

    public AdminTagsController(TagsService tagsService) {
        super(tagsService);
    }

    @PostMapping
    public Tag create(@Valid @RequestBody Tag tag) {
        return this.tagsService.save(tag);
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
