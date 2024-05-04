package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.persistence.tags.repo.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagsService {

    private TagRepository tagRepository;

    public TagsService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag save(Tag tag) {
        return this.tagRepository.save(tag);
    }

    public List<Tag> findAll() {
        return this.tagRepository.findAll();
    }

    public Tag findById(Long id) {
        return this.tagRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void deleteById(Long id) {
        Tag tag = this.tagRepository.findById(id).orElseThrow(NotFoundException::new);
        this.tagRepository.delete(tag);
    }

}
