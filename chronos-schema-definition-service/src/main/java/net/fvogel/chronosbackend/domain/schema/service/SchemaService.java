package net.fvogel.chronosbackend.domain.schema.service;

import jakarta.transaction.Transactional;
import net.fvogel.chronosbackend.commons.exception.NotFoundException;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.EntityAttributePORepository;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.EntityPORepository;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.RelationAttributePORepository;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.RelationPORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class SchemaService {

    @Autowired
    EntityPORepository entityPORepository;
    @Autowired
    EntityAttributePORepository entityAttributePORepository;
    @Autowired
    RelationPORepository relationPORepository;
    @Autowired
    RelationAttributePORepository relationAttributePORepository;

    public Set<EntityPO> allEntities() {
        return new HashSet<>(this.entityPORepository.findAll());
    }

    public EntityPO getEntityByKey(String key) {
        return this.entityPORepository.findByKey(key).orElseThrow(NotFoundException::new);
    }

    public EntityPO save(EntityPO entityPO) {
        for (EntityAttributePO attribute : entityPO.getAttributes()) {
            this.entityAttributePORepository.save(attribute);
        }
        return this.entityPORepository.save(entityPO);
    }

    public void delete(String key) {
        EntityPO entityPO = this.getEntityByKey(key);
        this.entityPORepository.delete(entityPO);
    }

    public RelationPO save(RelationPO relationPO) {
        for (RelationAttributePO attribute : relationPO.getAttributes()) {
            this.relationAttributePORepository.save(attribute);
        }
        return this.relationPORepository.save(relationPO);
    }

}
