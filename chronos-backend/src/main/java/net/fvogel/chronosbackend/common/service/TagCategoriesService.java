package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.tags.model.TagCategory;
import net.fvogel.chronosbackend.common.persistence.tags.repo.TagCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagCategoriesService {

    private TagCategoryRepository tagCategoryRepository;

    public TagCategoriesService(TagCategoryRepository tagCategoryRepository) {
        this.tagCategoryRepository = tagCategoryRepository;
    }

    public TagCategory save(TagCategory tagCategory) {
        return this.tagCategoryRepository.save(tagCategory);
    }

    public List<TagCategory> findAll() {
        return this.tagCategoryRepository.findAll();
    }

    public TagCategory findById(Long id) {
        return this.tagCategoryRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void deleteById(Long id) {
        TagCategory tagCategory = this.tagCategoryRepository.findById(id).orElseThrow(NotFoundException::new);
        this.tagCategoryRepository.delete(tagCategory);
    }

}
