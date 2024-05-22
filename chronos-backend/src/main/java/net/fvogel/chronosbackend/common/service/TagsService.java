package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.persistence.tags.repo.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagsService {

    private static final Logger logger = LoggerFactory.getLogger(TagsService.class);

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

    public Set<Tag> findByIds(Collection<Long> ids) {
        Set<Tag> result = this.tagRepository.findByIdIn(ids);
        if (ids.size() > result.size()) {
            this.logger.warn(
                    "Not able to find tags for all ids; searched for " + ids + ", but found only tags for IDs "
                    + result.stream().map(t -> t.getId()).collect(Collectors.toList())
            );
        }
        return result;
    }

    public void deleteById(Long id) {
        Tag tag = this.tagRepository.findById(id).orElseThrow(NotFoundException::new);
        this.tagRepository.delete(tag);
    }

}
