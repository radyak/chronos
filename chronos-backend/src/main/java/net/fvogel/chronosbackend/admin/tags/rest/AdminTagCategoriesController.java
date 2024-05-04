package net.fvogel.chronosbackend.admin.tags.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.tags.model.TagCategory;
import net.fvogel.chronosbackend.common.service.TagCategoriesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tag-categories")
public class AdminTagCategoriesController {

    private TagCategoriesService tagCategoriesService;

    public AdminTagCategoriesController(TagCategoriesService tagCategoriesService) {
        this.tagCategoriesService = tagCategoriesService;
    }

    @PostMapping
    public TagCategory create(@Valid @RequestBody TagCategory tagCategory) {
        return this.tagCategoriesService.save(tagCategory);
    }

    @GetMapping
    public List<TagCategory> all() {
        return this.tagCategoriesService.findAll();
    }

    @GetMapping("/{id}")
    public TagCategory getById(@PathVariable("id") Long id) {
        return this.tagCategoriesService.findById(id);
    }

    @PutMapping
    public TagCategory update(@Valid @RequestBody TagCategory tagCategory) {
        return this.tagCategoriesService.save(tagCategory);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        this.tagCategoriesService.deleteById(id);
    }

}
