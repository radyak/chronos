package net.fvogel.chronosbackend.open.rest;

import net.fvogel.chronosbackend.common.persistence.tags.model.TagCategory;
import net.fvogel.chronosbackend.common.service.TagCategoriesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tag-categories")
public class TagCategoriesController {

    private TagCategoriesService tagCategoriesService;

    public TagCategoriesController(TagCategoriesService tagCategoriesService) {
        this.tagCategoriesService = tagCategoriesService;
    }

    @GetMapping
    public List<TagCategory> all() {
        return this.tagCategoriesService.findAll();
    }

}
