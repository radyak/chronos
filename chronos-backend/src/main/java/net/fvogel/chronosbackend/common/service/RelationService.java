package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.relations.model.Relation;
import net.fvogel.chronosbackend.common.persistence.relations.repo.RelationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RelationService {

    private RelationRepository relationRepository;

    public RelationService(RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    public Relation save(Relation relation) {
        return this.relationRepository.save(relation);
    }

    public List<Relation> findAll() {
        return this.relationRepository.findAll();
    }

    public Relation findById(Long id) {
        return this.relationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Collection<Relation> findByEntryId(Long entryId) {
        return this.relationRepository.findByEntryId(entryId);
    }

    public void deleteById(Long id) {
        Relation relation = this.relationRepository.findById(id).orElseThrow(NotFoundException::new);
        this.relationRepository.delete(relation);
    }

}
