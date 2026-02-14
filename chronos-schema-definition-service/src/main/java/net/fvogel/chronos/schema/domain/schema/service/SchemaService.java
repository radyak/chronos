package net.fvogel.chronos.schema.domain.schema.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.schema.config.caching.CachingConfig;
import net.fvogel.chronos.schema.domain.schema.business.DefaultEntityAttributesRule;
import net.fvogel.chronos.schema.domain.schema.business.UniqueValidator;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.EntityAttributePORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.EntityPORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.RelationAttributePORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.RelationPORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
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
    @Autowired
    DefaultEntityAttributesRule defaultEntityAttributesRule;
    @Autowired
    UniqueValidator uniqueValidator;


    @Cacheable({CachingConfig.CacheNames.SCHEMA_CACHE})
    public Set<EntityPO> allEntities() {
        return new HashSet<>(this.entityPORepository.findAll());
    }

    @Cacheable({CachingConfig.CacheNames.ENTITY_CACHE})
    public EntityPO getEntityByKey(String key) {
        return this.entityPORepository.findByKey(key).orElseThrow(NotFoundException::new);
    }

    public void assertEntityExistsByKey(String key) {
        getEntityByKey(key);
    }

    @CacheEvict({CachingConfig.CacheNames.SCHEMA_CACHE})
    @CachePut(cacheNames = {CachingConfig.CacheNames.ENTITY_CACHE}, key = "#entityPO.key")
    public EntityPO save(EntityPO entityPO) {
        uniqueValidator.validate(entityPO);

        // Avoid persisting of default attributes
        List<EntityAttributePO> specificAttributes = entityPO.getAttributes().stream()
                .filter(attr -> defaultEntityAttributesRule.isDefaultEntityAttribute(attr.getKey()))
                .toList();
        entityPO.setAttributes(specificAttributes);

        for (EntityAttributePO attribute : entityPO.getAttributes()) {
            this.entityAttributePORepository.save(attribute);
        }

        EntityPO result = this.entityPORepository.save(entityPO);

        for (RelationPO relationPO : entityPO.getRelations()) {
            relationPO.setSource(entityPO);
            this.save(relationPO);
        }

        return result;
    }

    @CacheEvict({
            CachingConfig.CacheNames.ENTITY_CACHE,
            CachingConfig.CacheNames.SCHEMA_CACHE,
    })
    public void delete(String key) {
        EntityPO entityPO = this.getEntityByKey(key);
        this.entityPORepository.delete(entityPO);
    }

    public RelationPO save(RelationPO relationPO) {
        uniqueValidator.validate(relationPO);

        // Avoid persisting of default attributes
        List<RelationAttributePO> specificAttributes = relationPO.getAttributes().stream()
                .filter(attr -> defaultEntityAttributesRule.isDefaultRelationAttribute(attr.getKey()))
                .toList();
        relationPO.setAttributes(specificAttributes);

        for (RelationAttributePO attribute : relationPO.getAttributes()) {
            this.relationAttributePORepository.save(attribute);
        }
        return this.relationPORepository.save(relationPO);
    }

}
