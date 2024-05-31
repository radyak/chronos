package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.relations.model.RelationType;
import net.fvogel.chronosbackend.common.persistence.relations.repo.RelationTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RelationTypeService {

    private RelationTypeRepository relationTypeRepository;

    public RelationTypeService(RelationTypeRepository relationTypeRepository) {
        this.relationTypeRepository = relationTypeRepository;
    }

    public RelationType save(RelationType relationType) {
        return this.relationTypeRepository.save(relationType);
    }

    public List<RelationType> findAll() {
        return this.relationTypeRepository.findAll();
    }

    public RelationType findById(Long id) {
        return this.relationTypeRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void deleteById(Long id) {
        RelationType relationType = this.relationTypeRepository.findById(id).orElseThrow(NotFoundException::new);
        this.relationTypeRepository.delete(relationType);
    }

}
